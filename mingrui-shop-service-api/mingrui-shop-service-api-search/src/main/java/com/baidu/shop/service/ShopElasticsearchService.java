package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName ShopElasticsearchService
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/4
 * @Version V1.0
 **/
@Api(tags = "es接口")
public interface ShopElasticsearchService {

//    @ApiOperation(value = "获取商品信息测试")
//    @GetMapping(value ="es/goodsInfo")
//    Result<JSONObject> esGoodsInfo();

    @ApiOperation(value = "Es数据初始化 创建索引,创建映射,mysql数据同步")
    @GetMapping(value = "es/initGoodsEsData")
    Result<JSONObject> initGoodsEsData();

    @ApiOperation(value = "清空Es中的商品数据")
    @GetMapping(value = "es/clearGoodsEsData")
    Result<JSONObject> clearGoodsEsData();

    @ApiOperation(value = "搜索")
    @GetMapping(value = "es/search")
    Result<List<GoodsDoc>> search(@RequestParam String search,@RequestParam Integer page,@RequestParam String filter);

    @ApiOperation(value = "新增数据到es")
    @PostMapping(value = "es/saveData")
    Result<JSONObject> saveData(Integer spuId);

    @ApiOperation(value = "通过id删除es数据")
    @DeleteMapping(value = "es/saveData")
    Result<JSONObject> deleteData(Integer spuId);
}
