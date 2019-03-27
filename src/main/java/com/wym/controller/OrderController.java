package com.wym.controller;

import com.wym.service.OrderService;
import com.wym.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

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

    @PostMapping("/addcart")
    public Mono<ApiResult<Object>> addCart(@RequestParam String username, @RequestParam String bookId){
        log.info("addCart ~~ username = {}, bookId = {}", username, bookId);
        return orderService.addCart(username, bookId);
    }
}
