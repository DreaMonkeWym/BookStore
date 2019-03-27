package com.wym.service.impl;

import com.wym.mapper.AdminMapper;
import com.wym.mapper.UserMapper;
import com.wym.model.Admin;
import com.wym.model.User;
import com.wym.service.UserService;
import com.wym.utils.ApiResult;
import com.wym.utils.MD5Util;
import com.wym.utils.StaticConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Mono getLoginInfo(String username, String password){
        return Mono.fromSupplier(() -> {
            Mono<Admin> adminMono = selectAdminLogin(username);
            Mono<User> userMono = selectUserLogin(username);
            return Mono.zip(adminMono, userMono).map(tuple -> {
                if (!StringUtils.isEmpty(tuple.getT1().getAdminpassword()) && tuple.getT1().getAdminpassword().equals(MD5Util.encryptMD5(password))){
                    return ApiResult.getApiResult(201, "admin login success");
                } else if (!StringUtils.isEmpty(tuple.getT2().getPassword()) && tuple.getT2().getPassword().equals(MD5Util.encryptMD5(password))){
                    return ApiResult.getApiResult(200, "user login success");
                }
                return ApiResult.getApiResult(-1, "login fail");
            }).publishOn(Schedulers.elastic()).doOnError(t ->
                    log.error("getLoginInfo zip is error!~~,username == {}", username, t))
                    .onErrorReturn(ApiResult.getApiResult(-1, "login fail"));
        });
    }

    @Override
    public Mono getRegisterInfo(String username, String password, String favor, MultipartFile file) {
        return Mono.fromSupplier(() -> {
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
                        try {
                            file.transferTo(new File(filePath + File.separator + fileName));
                        } catch (IOException e) {
                            log.info("图片上传失败！ ~~", e);
                        }
                        return ApiResult.getApiResult(200, "user register success");
                    }
                }
                return  ApiResult.getApiResult(-1, "user register fail");
            }).publishOn(Schedulers.elastic()).doOnError(t ->
                    log.error("getRegisterInfo zip is error!~~,username == {}", username, t))
                    .onErrorReturn(ApiResult.getApiResult(-1, "user register fail"));
        });
    }
}
