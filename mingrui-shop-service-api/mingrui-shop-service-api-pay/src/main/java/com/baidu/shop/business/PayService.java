package com.baidu.shop.business;

import com.baidu.shop.dto.PayInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName PayService
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/16
 * @Version V1.0
 **/
public  interface PayService {

    @GetMapping(value = "pay/requestPay")//请求支付
    void requestPay(PayInfoDTO payInfoDTO, @CookieValue(value = "MRSHOP_TOKEN") String token, HttpServletResponse response);

    @GetMapping(value = "pay/notify")//通知接口,这个可能暂时测试不了
    void notify(HttpServletRequest httpServletRequest);

    @GetMapping(value = "pay/returnUrl")//跳转成功页面接口
    void returnUrl(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse);

}
