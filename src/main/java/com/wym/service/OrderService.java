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

    Mono<ApiResult<? extends List<CartDetail>>> queryCart(String username);
}
