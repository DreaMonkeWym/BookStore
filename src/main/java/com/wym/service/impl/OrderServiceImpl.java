package com.wym.service.impl;

import com.wym.mapper.BookDetailMapper;
import com.wym.mapper.CartMapper;
import com.wym.mapper.OrdersMapper;
import com.wym.model.BookDetail;
import com.wym.model.Cart;
import com.wym.model.Orders;
import com.wym.model.po.*;
import com.wym.service.OrderService;
import com.wym.utils.ApiResult;
import com.wym.utils.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            HashOperations<String, String, CartDetail> hashOperations = redisConfig.getRedisTemplate().opsForHash();
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
                        setCartDetail(cart, username);
                        return Mono.just(ApiResult.getApiResult(200, "Book add cart successfully "));
                    }
                }else {
                    String quantity = new BigInteger(carts.getQuantity()).add(new BigInteger("1")).toString();
                    // 是否超过图书总数 待实现
                    if (cartMapper.updateQuantity(carts.getCartid(), quantity) > 0){
                        CartDetail updatecart = hashOperations.get("queryCart" + username, carts.getCartid());
                        updatecart.setBookQuantity(quantity);
                        hashOperations.put("queryCart" + username, carts.getCartid(), updatecart);
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
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            HashOperations<String, String, CartDetail> hashOperations = redisConfig.getRedisTemplate().opsForHash();
            if (redisConfig.getRedisTemplate().hasKey("queryCart" + username)) {
                List<CartDetail> cartDetailList = hashOperations.values("queryCart" + username);
                return ApiResult.getApiResult(cartDetailList);
            }
            List<Cart> cartList = cartMapper.queryCart(username, false);
            List<CartDetail> cartDetailList = new ArrayList<>();
            if (!cartList.isEmpty()){
                cartList.forEach(cart -> {
                    CartDetail cartDetail = setCartDetail(cart, username);
                    cartDetailList.add(cartDetail);
                });
                return ApiResult.getApiResult(cartDetailList);
            }
            return ApiResult.getApiResult(new ArrayList<CartDetail>());
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryCart is error!~~ username = {}", username, t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
    }

    private CartDetail setCartDetail(Cart cart, String username) {
        RedisConfig redisConfig = new RedisConfig(redisTemplate);
        HashOperations<String, String, CartDetail> hashOperations = redisConfig.getRedisTemplate().opsForHash();
        CartDetail cartDetail = new CartDetail();
        BookDetail bookDetail = bookDetailMapper.selectByPrimaryKey(cart.getBookid());
        cartDetail.setCartId(cart.getCartid());
        cartDetail.setBookId(cart.getBookid());
        cartDetail.setBookName(bookDetail.getBookname());
        cartDetail.setBookAvatar(bookDetail.getAvatar());
        cartDetail.setBookPrice(bookDetail.getPrice());
        cartDetail.setBookQuantity(cart.getQuantity());
        cartDetail.setQuantity(bookDetail.getQuantity());
        hashOperations.put("queryCart" + username, cartDetail.getCartId(), cartDetail);
        return cartDetail;
    }

    @Override
    public Mono<ApiResult<Object>> delCartBook(String cartid, String username) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            HashOperations<String, String, CartDetail> hashOperations = redisConfig.getRedisTemplate().opsForHash();
            if (cartMapper.deleteByPrimaryKey(cartid) > 0){
                hashOperations.delete("queryCart" + username, cartid);
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
            if (cartMapper.updateQuantity(cartid, quantity) > 0){
                return ApiResult.getApiResult(200, "update the book successfully");
            }
            return ApiResult.getApiResult(-1, "update the book failly");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("updateCartBook is error!~~ cartId = {}, quantity = {}", cartid, quantity, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "update the book failly"));
    }

    @Override
    public Mono<ApiResult<Object>> delCartList(List<String> cartidList, String username) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            HashOperations<String, String, CartDetail> hashOperations = redisConfig.getRedisTemplate().opsForHash();
            cartidList.forEach(cartid -> {
                hashOperations.delete("queryCart" + username, cartid);
                cartMapper.deleteByPrimaryKey(cartid);
            });
            return ApiResult.getApiResult(200, "del books successfully");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("delCartList is error!~~ cartidList = {}", cartidList, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "del books failly"));
    }

    @Override
    public Mono<ApiResult<Object>> updateCartList(List<CartDetail> cartDetailList, String username) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            HashOperations<String, String, CartDetail> hashOperations = redisConfig.getRedisTemplate().opsForHash();
            if (!CollectionUtils.isEmpty(cartDetailList) && cartDetailList.size() > 0){
                cartDetailList.forEach(cartDetail ->{
                    CartDetail updatecart = hashOperations.get("queryCart" + username, cartDetail.getCartId());
                    updatecart.setBookQuantity(cartDetail.getBookQuantity());
                    hashOperations.put("queryCart" + username, cartDetail.getCartId(), updatecart);
                    cartMapper.updateQuantity(cartDetail.getCartId(),cartDetail.getBookQuantity());
                });
                return ApiResult.getApiResult(200, "updateCartList successfully");
            }
            return ApiResult.getApiResult(-1, "updateCartList failly");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("updateCartList is error!~~ cartDetailList = {}", cartDetailList, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "updateCartList failly"));
    }

    @Override
    public Mono<ApiResult<Object>> commitCartList(List<CartDetail> cartDetailList, String username) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            HashOperations<String, String, CartDetail> hashOperations = redisConfig.getRedisTemplate().opsForHash();
            ValueOperations<String, List<BookRecommend>> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            String orderid = System.currentTimeMillis() + username;
            if (!CollectionUtils.isEmpty(cartDetailList) && cartDetailList.size() > 0){
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
                        hashOperations.delete("queryCart" + username, cartDetail.getCartId());
                    }
                });
                valueOperations.getOperations().delete("queryOrderSold");
                return ApiResult.getApiResult(200, "commit cart successfully ");
            }
            return ApiResult.getApiResult(-1, "commit cart failly");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("commitCartList is error!~~ cartDetailList = {}, username = {}", cartDetailList, username, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "commit cart failly"));
    }

    @Override
    public Mono<ApiResult<Object>> delOrder(String orderId) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, ResOrder> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            if (redisConfig.getRedisTemplate().hasKey(orderId)){
                if (valueOperations.getOperations().delete(orderId) && ordersMapper.deleteOrder(orderId) > 0){
                    return ApiResult.getApiResult(200, "del the order successfully");
                }
            }else {
                if (ordersMapper.deleteOrder(orderId) > 0){
                    return ApiResult.getApiResult(200, "del the order successfully");
                }
            }
            return ApiResult.getApiResult(-1, "del the order failly");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("delOrder is error!~~ orderId = {}", orderId, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "del the order failly"));
    }

    @Override
    public Mono<ApiResult<Object>> delOrderList(List<String> orderIdList) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, ResOrder> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            orderIdList.forEach(orderId -> {
                valueOperations.getOperations().delete(orderId);
                ordersMapper.deleteOrder(orderId);
            });
            return ApiResult.getApiResult(200, "del the orders successfully");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("delOrder is error!~~ orderIdList = {}", orderIdList, t))
                .onErrorReturn(ApiResult.getApiResult(-1, "del the orders failly"));
    }

    @Override
    public Mono<ApiResult<List<ResOrder>>> queryById(String orderId) {
        return Mono.fromSupplier(() -> {
            List<ResOrder> resOrderList = new ArrayList<>();
            ResOrder resOrder = queryOrder(orderId);
            if (!Objects.isNull(resOrder)){
                resOrderList.add(resOrder);
                return ApiResult.getApiResult(resOrderList);
            }
            return ApiResult.getApiResult(resOrderList);
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("queryById is error!~~ orderId = {}", orderId, t))
                .onErrorReturn(ApiResult.getApiResult(new ArrayList<>()));
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
        RedisConfig redisConfig = new RedisConfig(redisTemplate);
        ValueOperations<String, ResOrder> valueOperations = redisConfig.getRedisTemplate().opsForValue();
        if (redisConfig.getRedisTemplate().hasKey(orderId)){
            return valueOperations.get(orderId);
        }else {
            List<Orders> ordersList = ordersMapper.queryByPrimaryKey(orderId);
            ResOrder resOrder = new ResOrder();
            List<OrderDetail> orderDetailList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(ordersList) && ordersList.size() > 0) {
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
            if (!CollectionUtils.isEmpty(resOrder.getOrderDetailList()) && resOrder.getOrderDetailList().size() > 0){
                valueOperations.set(orderId, resOrder);
            }
            return resOrder;
        }
    }
}
