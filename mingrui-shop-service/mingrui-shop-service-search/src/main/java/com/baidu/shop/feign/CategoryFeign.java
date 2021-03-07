package com.baidu.shop.feign;

import com.baidu.shop.service.CategoryService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName categoryFeign
 * @Description: TODO
 * @Author yyq
 * @Date 2021/3/6
 * @Version V1.0
 **/
@FeignClient(value = "xxx-server",contextId = "categoryFeign")
public interface CategoryFeign extends CategoryService {
}
