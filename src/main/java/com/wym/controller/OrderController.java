package com.wym.controller;

import com.wym.model.po.CartDetail;
import com.wym.service.OrderService;
import com.wym.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wym on 2019-03-26 18:43
 */

@RestController
@CrossOrigin
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 购物车添加商品
     * @param username
     * @param bookId
     * @return
     */
    @PostMapping("/addcart")
    public Mono<ApiResult<Object>> addCart(@RequestParam String username, @RequestParam String bookId){
        log.info("addCart ~~ username = {}, bookId = {}", username, bookId);
        return orderService.addCart(username, bookId);
    }

    /**
     * 查询购物车商品信息
     * @param username
     * @return
     */
    @GetMapping("/querycart")
    public Mono<ApiResult<? extends List<CartDetail>>> queryCart(@RequestParam String username){
        log.info("addCart ~ usernmae = {}", username);
        return orderService.queryCart(username);
    }

    /**
     * 购物车中删除单个商品
     * @param cartId
     * @return
     */
    @DeleteMapping("delcartbook")
    public Mono<ApiResult<Object>> delCartBook(@RequestParam String cartId){
        log.info("delCartBook ~ cartId = {}", cartId);
        return orderService.delCartBook(cartId);
    }

    /**
     * 购物车中删除多个商品
     * @param cartidList
     * @return
     */
    @DeleteMapping("delcartlist")
    public Mono<ApiResult<Object>> delCartList(@RequestParam List<String> cartidList){
        log.info("delCartList ~ cartidList = {}",cartidList);
        return orderService.delCartList(cartidList);
    }
    /**
     * 更改购物车商品数量
     * @param cartId
     * @param quantity
     * @return
     */
    @PutMapping("updatecartbook")
    public Mono<ApiResult<Object>> updateCartBook(@RequestParam String cartId, @RequestParam String quantity){
        log.info("updateCartBook ~ cartId = {}, quantity = {}", cartId, quantity);
        return orderService.updateCartBook(cartId, quantity);
    }

    /**
     * 更改购物车商品数量List
     * @param cartDetailList
     * @return
     */
    @PutMapping("/updatecartlist")
    public Mono<ApiResult<Object>> updateCartList(@RequestBody List<CartDetail> cartDetailList) {
        log.info("updateCartList ~cartDetailList = {}", cartDetailList);
        return orderService.updateCartList(cartDetailList);
    }
}
