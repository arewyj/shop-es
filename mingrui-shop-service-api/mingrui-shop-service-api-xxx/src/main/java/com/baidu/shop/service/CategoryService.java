package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CategoryService
 * @Description: TODO
 * @Author wyj
 * @Date 2020/12/22
 * @Version V1.0
 **/
@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "通过品牌Id查询分类信息")
    @GetMapping(value = "/category/brand")
    Result<List<CategoryEntity>> getCategoryByBrandId(Integer brandId);

    @ApiOperation(value = "通过查询商品分类")
    @GetMapping(value = "/category/list")
    Result<List<CategoryEntity>> getCategoryByPid(Integer pid);

    @ApiOperation(value = "通过Id删除分类")
    @DeleteMapping(value = "/category/delete")//前台请求方法   方法名必须一致
    Result<JsonObject> delCateGoryById(Integer id);

    @ApiOperation(value = "修改分类")
    @PutMapping(value = "/category/edit")//前台请求方法   方法名必须一致
    Result<JsonObject> editCateGoryById(@Validated({MingruiOperation.Update.class}) @RequestBody CategoryEntity entity);

    @ApiOperation(value = "新增分类")
    @PostMapping(value = "/category/save")
    Result<JsonObject> saveCateGory(@Validated({MingruiOperation.Add.class})@RequestBody CategoryEntity categoryEntity);

    @ApiOperation("通过Id集合查询分类")
    @GetMapping(value = "/category/getCategoryByIdList")
    Result<List<CategoryEntity>> getCategoryByIdList(@RequestParam String ids);
}
