package com.baidu.shop.business;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.OrderInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName OrderService
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/15
 * @Version V1.0
 **/
@Api(tags = "订单接口")
public interface OrderService {


    @ApiOperation(value = "创建订单")
    @PostMapping(value = "order/createOrder")
    Result<String> createOrder(@RequestBody OrderDTO orderDTO,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "通过orderId查询订单信息")
    @GetMapping(value = "order/getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam Long orderId);

}
