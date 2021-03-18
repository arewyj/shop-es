package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.TemplateService;
import com.baidu.shop.utils.BaiduBeanUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/9
 * @Version V1.0
 **/
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    private final Integer CREATE_STATIC_HTML = 1;
    private final Integer DELETE_STATIC_HTML = 2;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private BrandFeign brandFeign;

    @Resource
    private CategoryFeign categoryFeign;

    @Resource
    private GoodsFeign goodsFeign;

    @Resource
    private SpecificationFeign specificationFeign;

    @Autowired
    private TemplateEngine templateEngine;

    @Value(value = "${mrshop.static.html.path}")
    private String htmlPath;

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {
        Map<String, Object> goodsInfo = this.getGoodsInfo(spuId);
        Context context = new Context();
        context.setVariables(goodsInfo);

        File file = new File(htmlPath, spuId + ".html");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file, "UTF-8");
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            if(ObjectUtil.isNotNull(writer))
                writer.close();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {
        this.operationStaticHTML(CREATE_STATIC_HTML);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearStaticHTMLTemplate() {
        this.operationStaticHTML(DELETE_STATIC_HTML);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteStaticHTMLTemplate(Integer spuId) {
        File file = new File(htmlPath, spuId + ".html");
        if (file.exists()){
            file.delete();
        }
        return this.setResultSuccess();
    }

    private Boolean operationStaticHTML(Integer operation){
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(new SpuDTO());
        try {
            if (spuInfo.isSuccess()){
                spuInfo.getData().stream().forEach(spuDTO -> {
                    if (operation == 1){
                        this.createStaticHTMLTemplate(spuDTO.getId());
                    }else {
                        this.deleteStaticHTMLTemplate(spuDTO.getId());
                    }
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  false;
    }

    private Map<String, Object> getGoodsInfo(Integer spuId) {

        Map<String, Object> goodsInfoMap = new HashMap<>();
        //spu
        SpuDTO spuResultData = this.getSpuInfo(spuId);
        goodsInfoMap.put("spuInfo",spuResultData);
        //spuDetail
        goodsInfoMap.put("spuDetail",this.getSpuDetail(spuId));
        //分类信息
        goodsInfoMap.put("categoryInfo",this.getCategoryInfo(spuResultData.getCid1() + ""
                ,spuResultData.getCid2() + "",spuResultData.getCid3() + ""));
        //品牌信息
        goodsInfoMap.put("brandInfo",this.getBrandInfo(spuResultData.getBrandId()));
        //sku
        goodsInfoMap.put("skusInfo",this.getSkus(spuId));
        //规格组,规格参数(通用)
        goodsInfoMap.put("specParamAndGroup",this.getSpecGroupAndParam(spuResultData.getCid3()));
        //特殊规格
        goodsInfoMap.put("specParamMap",this.getSpecParamMap(spuResultData.getCid3()));

        return goodsInfoMap;
    }

    //获取spu信息
    private SpuDTO getSpuInfo(Integer spuId){
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> spuResult = goodsFeign.getSpuInfo(spuDTO);
        SpuDTO spuResultData = null;
        if(spuResult.isSuccess()){
            spuResultData = spuResult.getData().get(0);
        }
        return spuResultData;
    }
    //获取spuDetail
    private SpuDetailEntity getSpuDetail(Integer spuId){

        SpuDetailEntity spuDetailEntity = null;
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailByspuId(spuId);
        if(spuDetailResult.isSuccess()){
            spuDetailEntity = spuDetailResult.getData();
        }
        return spuDetailEntity;
    }

    //获取分类信息
    private List<CategoryEntity> getCategoryInfo(String cid1,String cid2,String cid3){

        List<CategoryEntity> categoryEntityList = null;

        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIdList(
                String.join(
                        ","
                        , Arrays.asList(cid1, cid2, cid3)
                )
        );
        if(categoryResult.isSuccess()){
            categoryEntityList = categoryResult.getData();
        }
        return categoryEntityList;
    }

    //获取品牌信息
    private BrandeEntity getBrandInfo(Integer brandId){

        BrandeEntity brandEntity = null;

        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(brandId);
        Result<PageInfo<BrandeEntity>> brandResult = brandFeign.getBrandInfo(brandDTO);
        if(brandResult.isSuccess()){
            brandEntity = brandResult.getData().getList().get(0);
        }

        return brandEntity;
    }

    //获取kus
    private List<SkuDTO> getSkus(Integer spuId){

        List<SkuDTO> skuList = null;

        Result<List<SkuDTO>> skusResult = goodsFeign.getSkusByspuId(spuId);
        if(skusResult.isSuccess()){
            skuList = skusResult.getData();
        }
        return skuList;
    }

    //获取规格组和通用规格参数
    private List<SpecGroupDTO> getSpecGroupAndParam(Integer cid3){

        List<SpecGroupDTO> specGroupAndParam = null;

        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setCid(cid3);
        Result<List<SpecGroupEntity>> specGroupResult = specificationFeign.getSpecGroupInfo(specGroupDTO);
        if(specGroupResult.isSuccess()){

            List<SpecGroupEntity> specGroupList = specGroupResult.getData();
            specGroupAndParam = specGroupList.stream().map(specGroup -> {
                SpecGroupDTO specGroupDTO1 = BaiduBeanUtils.copyProperties(specGroup, SpecGroupDTO.class);

                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setGroupId(specGroupDTO1.getId());
                specParamDTO.setGeneric(true);
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
                if (specParamResult.isSuccess()) {
                    specGroupDTO1.setSpecList(specParamResult.getData());
                }
                return specGroupDTO1;
            }).collect(Collectors.toList());
        }
        return specGroupAndParam;
    }

    //获取特殊规格参数
    private Map<Integer, String> getSpecParamMap(Integer cid3){

        Map<Integer, String> specParamMap = new HashMap<>();

        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(cid3);
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
        if(specParamResult.isSuccess()){
            List<SpecParamEntity> specParamEntityList = specParamResult.getData();
            specParamEntityList.stream().forEach(specParam -> specParamMap.put(specParam.getId(),specParam.getName()));
        }
        return specParamMap;
    }
}
