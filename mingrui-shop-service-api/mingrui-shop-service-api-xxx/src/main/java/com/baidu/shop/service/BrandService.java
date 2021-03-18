package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandeEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "品牌接口")
public interface BrandService {


    @ApiOperation(value = "获取品牌信息")
    @GetMapping(value = "/brand/list")
    Result<PageInfo<BrandeEntity>>  getBrandInfo(@SpringQueryMap BrandDTO brandDTO);

    @ApiOperation(value = "新增品牌信息")
    @PostMapping(value = "/brand/save")
    Result<JSONObject>  saveBrandInfo(@Validated({MingruiOperation.Add.class}) @RequestBody  BrandDTO brandDTO);

    @ApiOperation(value = "修改品牌信息")
    @PutMapping(value = "/brand/save")
    Result<JSONObject>  updateBrandInfo(@Validated({MingruiOperation.Update.class}) @RequestBody  BrandDTO brandDTO);

    @ApiOperation(value = "删除品牌")
    @DeleteMapping(value = "/brand/delete")
    Result<JSONObject>  deleteBrandInfo(Integer id);

    @ApiOperation(value = "根据分类Id查询品牌信息")
    @GetMapping(value = "/brand/getBrandByCategoryId")
    Result<List<BrandeEntity>>  getBrandByCategoryId(Integer cid);

    @ApiOperation(value = "通过Id集合查询品牌信息")
    @GetMapping(value = "/brand/getBrandByIdList")
    Result<List<BrandeEntity>> getBrandByIdList(@RequestParam String ids);
}
