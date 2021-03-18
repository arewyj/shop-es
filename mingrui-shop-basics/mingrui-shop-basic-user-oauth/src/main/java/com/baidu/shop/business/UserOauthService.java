package com.baidu.shop.business;

import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.entity.UserEntity;

/**
 * @ClassName UserOauthService
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/12
 * @Version V1.0
 **/
public interface UserOauthService {
    String login(UserEntity userEntity, JwtConfig jwtConfig);
}
