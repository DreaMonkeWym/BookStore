package com.wym.service;

import com.wym.utils.ApiResult;
import reactor.core.publisher.Mono;

/**
 * Created by wym on 2019-03-26 18:36
 */
public interface OrderService {

    /**
     * 购物车添加商品
     * @param username
     * @param bookId
     * @param quantity
     * @return
     */
    Mono<ApiResult<Object>> addCart(String username, String bookId);
}
