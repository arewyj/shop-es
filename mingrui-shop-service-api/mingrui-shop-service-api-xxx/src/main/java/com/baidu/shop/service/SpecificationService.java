package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("规格接口")
public interface SpecificationService {
    @ApiOperation("通过条件查询规则数据")
    @GetMapping("specgroup/getSpecGroupInfo")
    Result<List<SpecGroupEntity>> getSpecGroupInfo(SpecGroupDTO specGroupDTO);

    @ApiOperation("新增规则数据")
    @PostMapping("specgroup/save")
    Result<JSONObject> saveSpecGroupInfo(@Validated({MingruiOperation.Add.class}) @RequestBody  SpecGroupDTO specGroupDTO);

    @ApiOperation("修改规则数据")
    @PutMapping("specgroup/save")
    Result<JSONObject> editSpecGroupInfo(@Validated({MingruiOperation.Update.class}) @RequestBody  SpecGroupDTO specGroupDTO);

    @ApiOperation("删除规则数据")
    @DeleteMapping("specgroup/delete")
    Result<JSONObject> deleteSpecGroupInfo(Integer id);

    @ApiOperation("通过条件查询规则参数")
    @GetMapping("specparam/getSpecParamInfo")
    Result<List<SpecParamEntity>> getSpecParamInfo(@SpringQueryMap SpecParamDTO specParamDTO);

    @ApiOperation("新增规则参数")
    @PostMapping("specparam/saveSpecParamInfo")
    Result<JSONObject>  saveSpecParamInfo(@Validated({MingruiOperation.Add.class})@RequestBody SpecParamDTO specParamDTO);

    @ApiOperation("修改规则参数")
    @PutMapping("specparam/saveSpecParamInfo")
    Result<JSONObject>  editSpecParamInfo(@Validated({MingruiOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation("删除规则参数")
    @DeleteMapping("specparam/delete")
    Result<JSONObject>  deleteSpecParamInfo(Integer id);
}
