package com.wym.service.impl;

import com.wym.mapper.AdminMapper;
import com.wym.mapper.UserMapper;
import com.wym.model.Admin;
import com.wym.model.User;
import com.wym.model.po.ResUser;
import com.wym.service.UserService;
import com.wym.utils.ApiResult;
import com.wym.utils.MD5Util;
import com.wym.utils.RedisConfig;
import com.wym.utils.StaticConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * Created by wym on 2019-03-12 13:34
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper udao;
    @Resource
    private AdminMapper adao;
    @Resource
    private StaticConfig staticConfig;
    @Resource
    private RedisTemplate redisTemplate;

    private Mono<Admin> selectAdminLogin(String adminname) {
        return Mono.fromSupplier(() -> {
            if (adao.selectByPrimaryKey(adminname) != null){
                return adao.selectByPrimaryKey(adminname);
            }
            return new Admin();
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("selectAdminLogin is error!~~,adminname == {}", adminname,t))
                .onErrorReturn(new Admin());
    }
    public Mono<User> selectUserLogin(String username) {
        return Mono.fromSupplier(() -> {
            if (udao.selectByPrimaryKey(username) != null){
                return udao.selectByPrimaryKey(username);
            }
            return new User();
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("selectUserLoginis error!~~,username == {}", username,t))
                .onErrorReturn(new User());
    }
    @Override
    public Mono getLoginInfo(String username, String password) {
        return Mono.fromSupplier(() -> {
//            String token = System.currentTimeMillis() + username ; 有些复杂，是否完成待定，现模拟单点登录
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, User> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            ValueOperations<String, Admin> adminValueOperations = redisConfig.getRedisTemplate().opsForValue();
            if (redisConfig.getRedisTemplate().hasKey("recentUser")) {
                User user = valueOperations.get("recentUser");
                if (username.equals(user.getUsername())) {
                    return ApiResult.getApiResult(-1, "You are logged in ! ~ ");
                }
            } else if (redisConfig.getRedisTemplate().hasKey("recentAdmin")) {
                Admin admin = adminValueOperations.get("recentAdmin");
                if (username.equals(admin.getAdminname())) {
                    return ApiResult.getApiResult(-1, "You are logged in ! ~ ");
                }
            }
            ValueOperations<String, Admin> valueOperation = redisConfig.getRedisTemplate().opsForValue();
            if (redisConfig.getRedisTemplate().hasKey("admin" + username)) {
                if (valueOperation.get("admin" + username).getAdminname().equals(username) &&
                        valueOperation.get("admin" + username).getAdminpassword().equals(MD5Util.encryptMD5(password))) {
                    Admin admin = new Admin();
                    admin.setAdminname(username);
                    adminValueOperations.set("recentAdmin", admin);
                    ResUser resUser = new ResUser();
                    resUser.setUsername(username);
                    resUser.setIsRoot(true);
                    return ApiResult.getApiResult("admin login success", resUser);
                }
                    return ApiResult.getApiResult(-1, "login fail");
            } else if (redisConfig.getRedisTemplate().hasKey("user" + username)){
                if (valueOperations.get("user" + username).getUsername().equals(username) &&
                        valueOperations.get("user" + username).getPassword().equals(MD5Util.encryptMD5(password))) {
                    User user = new User();
                    user.setUsername(username);
                    valueOperations.set("recentUser", user);
                    ResUser resUser = new ResUser();
                    resUser.setUsername(username);
                    resUser.setIsRoot(false);
                    return ApiResult.getApiResult( "user login success", resUser);
                }
                    return ApiResult.getApiResult(-1, "login fail");
            }
            Mono<Admin> adminMono = selectAdminLogin(username);
            Mono<User> userMono = selectUserLogin(username);
            return Mono.zip(adminMono, userMono).map(tuple -> {
                if (!StringUtils.isEmpty(tuple.getT1().getAdminpassword()) && tuple.getT1().getAdminpassword().equals(MD5Util.encryptMD5(password))){
                    valueOperation.set("admin" + username, tuple.getT1());
                    Admin admin = new Admin();
                    admin.setAdminname(username);
                    adminValueOperations.set("recentAdmin", admin);
                    ResUser resUser = new ResUser();
                    resUser.setUsername(username);
                    resUser.setIsRoot(true);
                    return ApiResult.getApiResult("admin login success", resUser);
                } else if (!StringUtils.isEmpty(tuple.getT2().getPassword()) && tuple.getT2().getPassword().equals(MD5Util.encryptMD5(password))){
                    valueOperations.set("user" + username, tuple.getT2());
                    valueOperations.set("recentUser", tuple.getT2());
                    ResUser resUser = new ResUser();
                    resUser.setUsername(username);
                    resUser.setIsRoot(false);
                    return ApiResult.getApiResult( "user login success", resUser);
                }
                return ApiResult.getApiResult(-1, "login fail");
            }).publishOn(Schedulers.elastic()).doOnError(t ->
                    log.error("getLoginInfo zip is error!~~, username == {}", username, t))
                    .onErrorReturn(ApiResult.getApiResult(-1, "login fail"));
        });
    }

    @Override
    public Mono getRegisterInfo(String username, String password, String favor, MultipartFile file) {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, User> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            Mono<Admin> adminMono = selectAdminLogin(username);
            Mono<User> userMono = selectUserLogin(username);
            return Mono.zip(adminMono, userMono).map(tuple ->{
                if (StringUtils.isEmpty(tuple.getT1().getAdminpassword())
                            && StringUtils.isEmpty(tuple.getT2().getPassword())){
                    String fileName = "";
                    String filePath = staticConfig.getFilePath();
                    if(!file.isEmpty()){
                        fileName = file.getOriginalFilename();
                        String suffixName = fileName.substring(fileName.lastIndexOf("."));
                        fileName = username + suffixName;
                    }
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(MD5Util.encryptMD5(password));
                    user.setFavor(favor);
                    user.setAvatar(fileName);
                    if (udao.insert(user) > 0){
                        valueOperations.set("user"+username, user);
                        try {
                            file.transferTo(new File(filePath + File.separator + fileName));
                        } catch (IOException e) {
                            log.info("图片上传失败！ ~~", e);
                        }
                        return ApiResult.getApiResult(200, "user register success");
                    }
                }
                return  ApiResult.getApiResult(-100, "user register fail");
            }).publishOn(Schedulers.elastic()).doOnError(t ->
                    log.error("getRegisterInfo zip is error!~~,username == {}", username, t))
                    .onErrorReturn(ApiResult.getApiResult(-1, "user register fail"));
        });
    }

    @Override
    public Mono<ApiResult<Object>> getLogoutInfo() {
        return Mono.fromSupplier(() -> {
            RedisConfig redisConfig = new RedisConfig(redisTemplate);
            ValueOperations<String, User> valueOperations = redisConfig.getRedisTemplate().opsForValue();
            ValueOperations<String, Admin> valueOperation= redisConfig.getRedisTemplate().opsForValue();
            if (redisConfig.getRedisTemplate().hasKey("recentUser")) {
                valueOperations.getOperations().delete("recentUser");
                return  ApiResult.getApiResult(200, "user logout successfully");
            } else if (redisConfig.getRedisTemplate().hasKey("recentAdmin")) {
                valueOperation.getOperations().delete("recentAdmin");
                return  ApiResult.getApiResult(200, "admin logout successfully");
            }
            return  ApiResult.getApiResult(-1, "logout fail");
        }).publishOn(Schedulers.elastic()).doOnError(t ->
                log.error("getLogoutInfo error!~~ ", t))
                .onErrorReturn(ApiResult.getApiResult(-1, "logout fail"));
    }
}
