package com.wym.service.impl;

import com.wym.mapper.BookDetailMapper;
import com.wym.mapper.CartMapper;
import com.wym.mapper.OrdersMapper;
import com.wym.model.BookDetail;
import com.wym.model.Cart;
import com.wym.model.Orders;
import com.wym.model.po.CartDetail;
import com.wym.model.po.OrderDetail;
import com.wym.model.po.ResOrder;
import com.wym.service.OrderService;
import com.wym.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wym on 2019-03-26 18:37
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private CartMapper cartMapper;
    @Resource
    private BookDetailMapper bookDetailMapper;
    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private ReactiveRedisTemplate reactiveRedisTemplate;
    @Resource
    private RedisTemplate redisTemplate;

    private Mono<Cart> selectBookExist(String username, String bookId){
        return Mono.fromSupplier(() -> {
            if(!Objects.isNull(cartMapper.selectBookExist(username, bookId, false))){
                return cartMapper.selectBookExist(username, bookId, false);
            }
            return new Cart();
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("selectBookExist is error!~~,username = {}, bookId == {}", username, bookId, t))
                .onErrorReturn(new Cart());
    }

    @Override
    public Mono addCart(String username, String bookId) {
        return Mono.fromSupplier(() -> {
            Mono<Cart> cartMono = selectBookExist(username, bookId);
            return cartMono.flatMap(carts -> {
                if (StringUtils.isEmpty(carts.getCartid())){
                    Cart cart = new Cart();
                    cart.setCartid(System.currentTimeMillis() + username);
                    cart.setBookid(bookId);
                    cart.setUsername(username);
                    cart.setQuantity("1");
                    cart.setPayment(false);
                    if (cartMapper.insert(cart) > 0){
                        return Mono.just(ApiResult.getApiResult(200, "Book add cart successfully "));
                    }
                }else {
                    String quantity = new BigInteger(carts.getQuantity()).add(new BigInteger("1")).toString();
                    // 是否超过图书总数 待实现
                    if (cartMapper.updateQuantity(carts.getCartid(), quantity) > 0){
                        return Mono.just(ApiResult.getApiResult(200, "Book add cart successfully "));
                    }
                }
                return Mono.just(ApiResult.getApiResult(-1, "Book add cart failly "));
            }).publishOn(Schedulers.elastic()).doOnError(t ->
                    log.error("selectBookExist is error!~~,username = {}, bookId == {}", username, bookId, t))
                    .onErrorReturn(ApiResult.getApiResult(-1, "Book add cart failly "));
        });
    }

    @Override
    public Mono<ApiResult<? extends List<CartDetail>>> queryCart(String username) {
        return Mono.fromSupplier(() -> {
            List<Cart> cartList = cartMapper.queryCart(username, false);
            List<CartDetail> cartDetailList = new ArrayList<>();
            if (!cartList.isEmpty()){
                cartList.forEach(cart -> {
                    CartDetail cartDetail = new CartDetail();
                    BookDetail bookDetail = bookDetailMapper.selectByPrimaryKey(cart.getBookid());
                    cartDetail.setCartId(cart.getCartid());
                    cartDetail.setBookId(cart.getBookid());
                    cartDetail.setBookName(bookDetail.getBookname());
                    cartDetail.setBookAvatar(bookDetail.getAvatar());
                    cartDetail.setBookPrice(bookDetail.getPrice());
                    cartDetail.setBookQuantity(cart.getQuantity());
                    cartDetail.setQuantity(bookDetail.getQuantity());
                    cartDetailList.add(cartDetail);
                });
                return ApiResult.getApiResult(cartDetailList);
            }
            return ApiResult.getApiResult(new ArrayList<CartDetail>());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryCart is error!~~ username = {}", username, t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
    }

    @Override
    public Mono<ApiResult<Object>> delCartBook(String cartid) {
        return Mono.fromSupplier(() -> {
            if (cartMapper.deleteByPrimaryKey(cartid) > 0){
                return ApiResult.getApiResult(200,"del the book successfully");
            }
            return ApiResult.getApiResult(-1,"del the book failly");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("delCartBook is error!~~ cartId = {}", cartid, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "del the book failly"));
    }

    @Override
    public Mono<ApiResult<Object>> updateCartBook(String cartid, String quantity) {
        return Mono.fromSupplier(() -> {
            // 是否超过图书总数 待实现
            if (cartMapper.updateQuantity(cartid, quantity) > 0){
                return ApiResult.getApiResult(200, "update the book successfully");
            }
            return ApiResult.getApiResult(-1, "update the book failly");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("updateCartBook is error!~~ cartId = {}, quantity = {}", cartid, quantity, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "update the book failly"));
    }

    @Override
    public Mono<ApiResult<Object>> delCartList(List<String> cartidList) {
        return Mono.fromSupplier(() -> {
            cartidList.forEach(cartid -> cartMapper.deleteByPrimaryKey(cartid));
            return ApiResult.getApiResult(200, "del books successfully");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("delCartList is error!~~ cartidList = {}", cartidList, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "del books failly"));
    }

    @Override
    public Mono<ApiResult<Object>> updateCartList(List<CartDetail> cartDetailList) {
        return Mono.fromSupplier(() -> {
            cartDetailList.forEach(cartDetail ->
                    cartMapper.updateQuantity(cartDetail.getCartId(),cartDetail.getBookQuantity())
            );
            return ApiResult.getApiResult(200, "updateCartList successfully");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("updateCartList is error!~~ cartDetailList = {}", cartDetailList, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "updateCartList failly"));
    }

    @Override
    public Mono<ApiResult<Object>> commitCartList(List<CartDetail> cartDetailList, String username) {
        return Mono.fromSupplier(() -> {
            String orderid = System.currentTimeMillis() + username;
            cartDetailList.forEach(cartDetail -> {
                Orders orders = new Orders();
                orders.setOrderid(orderid);
                orders.setUsername(username);
                orders.setCartid(cartDetail.getCartId());
                if (ordersMapper.insert(orders) > 0){
                    BookDetail bookDetail = bookDetailMapper.selectByPrimaryKey(cartDetail.getBookId());
                    String quantity = new BigInteger(bookDetail.getQuantity()).subtract(new BigInteger(cartDetail.getBookQuantity())).toString();
                    String soldout = new BigInteger(bookDetail.getSoldout()).add(new BigInteger(cartDetail.getBookQuantity())).toString();
                    bookDetailMapper.updateQuantity(bookDetail.getBookid(), quantity, soldout);
                    cartMapper.updatePayment(cartDetail.getCartId(), true);
                }
            });
            return ApiResult.getApiResult(200, "commit cart successfully ");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("commitCartList is error!~~ cartDetailList = {}, username = {}", cartDetailList, username, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "commit cart failly"));
    }

    @Override
    public Mono<ApiResult<Object>> delOrder(String orderId) {
        return Mono.fromSupplier(() -> {
            if (ordersMapper.deleteOrder(orderId) > 0){
                return ApiResult.getApiResult(200, "del the order successfully");
            }
            return ApiResult.getApiResult(-1, "del the order failly");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("delOrder is error!~~ orderId = {}", orderId, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "del the order failly"));
    }

    @Override
    public Mono<ApiResult<Object>> delOrderList(List<String> orderIdList) {
        return Mono.fromSupplier(() -> {
            orderIdList.forEach(orderId -> ordersMapper.deleteOrder(orderId));
            return ApiResult.getApiResult(200, "del the orders successfully");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("delOrder is error!~~ orderIdList = {}", orderIdList, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "del the orders failly"));
    }

    @Override
    public Mono<ApiResult<ResOrder>> queryById(String orderId) {
        return Mono.fromSupplier(() -> {
            ResOrder resOrder = queryOrder(orderId);
            if (!Objects.isNull(resOrder)){
               //redis
                return ApiResult.getApiResult(resOrder);
            }
            return ApiResult.getApiResult(new ResOrder());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryById is error!~~ orderId = {}", orderId, t))
                .onErrorReturn(ApiResult.getApiResult(new ResOrder()));
    }

    @Override
    public Mono<ApiResult<? extends List<ResOrder>>> queryByName(String username) {
        return Mono.fromSupplier(() -> {
            List<Orders> ordersList = ordersMapper.queryByName(username);
            Set<String> orderSet = new HashSet<>();
            if (!ordersList.isEmpty()) {
                ordersList.forEach(orders -> orderSet.add(orders.getOrderid()));
            }
            List<ResOrder> resOrderList = new ArrayList<>();
            if (!orderSet.isEmpty()) {
                orderSet.forEach(orderId -> {
                    ResOrder resOrder = queryOrder(orderId);
                    resOrderList.add(resOrder);
                });
                return ApiResult.getApiResult(resOrderList);
            }
            return ApiResult.getApiResult(new ArrayList<ResOrder>());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryByName is error!~~ username = {}", username, t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
    }

    /**
     * 查询订单
     * @param orderId
     * @return
     */
    private ResOrder queryOrder(String orderId) {
        List<Orders> ordersList = ordersMapper.queryByPrimaryKey(orderId);
        ResOrder resOrder = new ResOrder();
        List<OrderDetail> orderDetailList = new ArrayList<>();
        if (!ordersList.isEmpty()) {
            ordersList.forEach(orders -> {
                OrderDetail orderDetail = new OrderDetail();
                Cart cart = cartMapper.selectByPrimaryKey(orders.getCartid());
                if (!Objects.isNull(cart)) {
                    BookDetail bookDetail = bookDetailMapper.selectByPrimaryKey(cart.getBookid());
                    if (!Objects.isNull(bookDetail)) {
                        orderDetail.setCartId(cart.getCartid());
                        orderDetail.setBookId(bookDetail.getBookid());
                        orderDetail.setAvatar(bookDetail.getAvatar());
                        orderDetail.setBookName(bookDetail.getBookname());
                        orderDetail.setPrice(bookDetail.getPrice());
                        orderDetail.setQuantity(cart.getQuantity());
                        String amount = new BigDecimal(orderDetail.getPrice()).multiply(new BigDecimal(orderDetail.getQuantity())).toEngineeringString();
                        orderDetail.setAmount(amount);
                        orderDetailList.add(orderDetail);
                    }
                }
            });
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            String orderDate = dateformat.format(Long.parseLong(orderId.substring(0, 13)));
            resOrder.setOrderId(orderId);
            resOrder.setOrderDate(orderDate);
            resOrder.setOrderDetailList(orderDetailList);
        }
        return resOrder;
    }
}
