package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName TemplateService
 * @Description: TODO
 * @Author yyq
 * @Date 2021/3/9
 * @Version V1.0
 **/
@Api(tags = "模板接口")
public interface TemplateService {
    @ApiOperation(value = "通过spuId创建html文件")
    @GetMapping(value = "template/createStaticHTMLTemplate")
    Result<JSONObject> createStaticHTMLTemplate(Integer spuId);

    @ApiOperation(value = "初始化html文件")
    @GetMapping(value = "template/initStaticHTMLTemplate")
    Result<JSONObject> initStaticHTMLTemplate();

    @ApiOperation(value = "清空html文件")
    @GetMapping(value = "template/clearStaticHTMLTemplate")
    Result<JSONObject> clearStaticHTMLTemplate();

    @ApiOperation(value = "通过spuId删除html文件")
    @GetMapping(value = "template/deleteStaticHTMLTemplate")
    Result<JSONObject> deleteStaticHTMLTemplate(Integer spuId);
}
