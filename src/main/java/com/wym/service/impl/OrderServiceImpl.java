package com.wym.service.impl;

import com.wym.mapper.CartMapper;
import com.wym.model.Cart;
import com.wym.service.OrderService;
import com.wym.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by wym on 2019-03-26 18:37
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private CartMapper cartMapper;

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
}
