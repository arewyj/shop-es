package com.baidu.shop.fegin;

import com.baidu.shop.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName GoodsFeign
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/4
 * @Version V1.0
 **/
@FeignClient(value = "xxx-server",contextId = "GoodsFeign")
public interface GoodsFeign extends GoodsService {

}
