package com.wym.controller;

import com.wym.service.UserService;
import com.wym.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by wym on 2019-03-12 13:32
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    /**
     *  管理员或用户登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/login")
    public Mono<ApiResult<Object>> getLoginInfo(@RequestParam String username, @RequestParam String password) {
        log.info("getLoginInfo username = {}, password = {}", username, password);
        return userService.getLoginInfo(username, password);
    }

    /**
     * 用户注册
     * @param username
     * @param password
     * @param favor
     * @param file
     * @return
     */
    @PostMapping("/register")
    public Mono<ApiResult<Object>> getRegisterInfo(@RequestParam String username,
                                                   @RequestParam String password,
                                                   @RequestParam String favor,
                                                   @RequestParam MultipartFile file){
        log.info("getRegisterInfo username = {}, password = {}", username, password);
        return userService.getRegisterInfo(username, password ,favor, file);
    }
}
