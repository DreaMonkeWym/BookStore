package com.wym.service;

import com.wym.model.po.CartDetail;
import com.wym.utils.ApiResult;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Created by wym on 2019-03-26 18:36
 */
public interface OrderService {

    /**
     * 购物车添加商品
     * @param username
     * @param bookId
     * @return
     */
    Mono<ApiResult<Object>> addCart(String username, String bookId);

    /**
     * 查询购物车
     * @param username
     * @return
     */
    Mono<ApiResult<? extends List<CartDetail>>> queryCart(String username);

    /**
     * 购物车中删除商品
     * @param cartid
     * @return
     */
    Mono<ApiResult<Object>> delCartBook(String cartid);
}
