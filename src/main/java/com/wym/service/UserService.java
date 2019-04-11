package com.wym.service;

import com.wym.model.User;
import com.wym.utils.ApiResult;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

/**
 * Created by wym on 2019-03-19 21:10
 */
public interface UserService {

     Mono<User> selectUserLogin(String username);

     /**
      * 管理员或用户登录
      * @param username
      * @param password
      * @return
      */
     Mono<ApiResult<Object>> getLoginInfo(String username, String password);

     /**
      * 用户注册
      * @param username
      * @param password
      * @param favor
      * @param file
      * @return
      */
     Mono<ApiResult<Object>> getRegisterInfo(String username, String password, String favor, MultipartFile file);

     /**
      * 用户注销
      * @return
      */
     Mono<ApiResult<Object>> getLogoutInfo();
}
