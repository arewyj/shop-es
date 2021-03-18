package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "商品接口")
public interface GoodsService {

    @ApiOperation(value = "获取商品信息")
    @GetMapping(value = "goods/getSpuInfo")
    Result<List<SpuDTO>>  getSpuInfo(@SpringQueryMap SpuDTO spuDTO);

    @ApiOperation(value = "新增商品信息")
    @PostMapping(value = "goods/save")
    Result<JSONObject> saveGoods(@Validated({MingruiOperation.Add.class}) @RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "修改商品信息")
    @PutMapping(value = "goods/save")
    Result<JSONObject> updateGoods(@Validated({MingruiOperation.Update.class}) @RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "根据spuId查询spuDetail信息")
    @GetMapping(value = "goods/getSpuDetailByspuId")
    Result<SpuDetailEntity>  getSpuDetailByspuId(@RequestParam Integer spuId);

    @ApiOperation(value = "根据spuId查询sku信息")
    @GetMapping(value = "goods/getSkusByspuId")
    Result<List<SkuDTO>>  getSkusByspuId(@RequestParam Integer spuId);

    @ApiOperation(value = "删除商品")
    @DeleteMapping(value = "goods/delete")
    Result<JSONObject>  delete(Integer spuId);

    @ApiOperation(value = "上下架")
    @PutMapping(value = "goods/updateSaleable")
    Result<JSONObject>  updateSaleable(@RequestBody  SpuDTO spuDTO);

    @ApiOperation(value = "根据skuId查询sku信息")
    @GetMapping(value = "goods/getSkuById")
    Result<SkuEntity> getSkuById(@RequestParam Long skuId);
}
