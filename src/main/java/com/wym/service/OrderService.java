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
     * 购物车中删除单个商品
     * @param cartid
     * @return
     */
    Mono<ApiResult<Object>> delCartBook(String cartid);

    /**
     * 购物车中删除多个商品
     * @param cartidList
     * @return
     */
    Mono<ApiResult<Object>> delCartList(List<String> cartidList);

    /**
     * 更改购物车商品数量
     * @param cartid
     * @param quantity
     * @return
     */
    Mono<ApiResult<Object>> updateCartBook(String cartid, String quantity);

    /**
     * 更改购物车商品数量List
     * @param cartDetailList
     * @return
     */
    Mono<ApiResult<Object>> updateCartList(List<CartDetail> cartDetailList);

    /**
     * 购物车提交订单
     * @param cartDetailList
     * @param username
     * @return
     */
    Mono<ApiResult<Object>> commitCartList(List<CartDetail> cartDetailList, String username);

}
