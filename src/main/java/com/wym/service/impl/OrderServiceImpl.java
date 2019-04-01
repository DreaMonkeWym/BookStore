package com.wym.service.impl;

import com.wym.mapper.BookDetailMapper;
import com.wym.mapper.CartMapper;
import com.wym.model.BookDetail;
import com.wym.model.Cart;
import com.wym.model.po.CartDetail;
import com.wym.service.OrderService;
import com.wym.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
//                    String amount = new BigDecimal(cartDetail.getBookPrice()).multiply(new BigDecimal(cartDetail.getBookQuantity())).toEngineeringString();
//                    cartDetail.setBookAmount(String.valueOf(amount));
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
    public Mono<ApiResult<Object>> delCartList(List<String> cartidList){
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
}
