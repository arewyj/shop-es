package com.baidu.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName RunOauthServerApplication
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/12
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan(value = "com.baidu.shop.mapper")
public class RunOauthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunOauthServerApplication.class);
    }
}
