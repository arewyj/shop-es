package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.PageService;
import com.baidu.shop.utils.BaiduBeanUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName PageServiceImpl
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/8
 * @Version V1.0
 **/
//@Service
public class PageServiceImpl implements PageService {

    //@Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    //@Resource
    private BrandFeign brandFeign;

    //@Resource
    private CategoryFeign categoryFeign;

    //@Resource
    private GoodsFeign goodsFeign;

    //@Resource
    private SpecificationFeign specificationFeign;

    @Override
    public Map<String, Object> getGoodsInfo(Integer spuId) {
        Map<String, Object> map = new HashMap<>();
        //spu信息
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);

        Result<List<SpuDTO>> spuResult = goodsFeign.getSpuInfo(spuDTO);
        SpuDTO spuResultData = null;
        if (spuResult.isSuccess()){
            spuResultData = spuResult.getData().get(0);
            map.put("spuInfo",spuResultData);
        }
        //spuDepail
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailByspuId(spuId);
        if (spuDetailResult.isSuccess()){
            map.put("spuDetail",spuDetailResult.getData());
        }
        //分类信息
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIdList(
                String.join(",", Arrays.asList(spuResultData.getCid1()+"", spuResultData.getCid2()+"", spuResultData.getCid3()+""))
        );
        if (categoryResult.isSuccess()){
            map.put("categoryInfo",categoryResult.getData());
        }
        //品牌信息
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(spuResultData.getBrandId());

        Result<PageInfo<BrandeEntity>> brandResult = brandFeign.getBrandInfo(brandDTO);
        if (brandResult.isSuccess()){
            map.put("brandInfo",brandResult.getData().getList().get(0));
        }
        //sku
        Result<List<SkuDTO>> skusResult = goodsFeign.getSkusByspuId(spuId);
        if (skusResult.isSuccess()){
            map.put("skusInfo",skusResult.getData());
        }
        //规格组,通用规格参数
        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setCid(spuResultData.getCid3());
        Result<List<SpecGroupEntity>> specGroupResult = specificationFeign.getSpecGroupInfo(specGroupDTO);

        if (specGroupResult.isSuccess()){
            List<SpecGroupEntity> specGroupList = specGroupResult.getData();
            List<SpecGroupDTO> specParamAndGroup = specGroupList.stream().map(specGroup -> {
                SpecGroupDTO specGroupDTO1 = BaiduBeanUtils.copyProperties(specGroup, SpecGroupDTO.class);

                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setGroupId(specGroupDTO1.getId());
                specParamDTO.setGeneric(true);

                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
                if (specParamResult.isSuccess()){
                    specGroupDTO1.setSpecList(specParamResult.getData());
                }
                return specGroupDTO1;
            }).collect(Collectors.toList());
            map.put("specParamAndGroup",specParamAndGroup);
        }
        //特殊规格参数
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuResultData.getCid3());
        specParamDTO.setGeneric(false);
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
        if (specParamResult.isSuccess()){
            List<SpecParamEntity> specParams = specParamResult.getData();
            Map<Integer, String> specParamMap = new HashMap<>();
            specParams.stream().forEach(specParam ->{
                specParamMap.put(specParam.getId(),specParam.getName());
                map.put("specParamMap",specParamMap);
            });
        }
        return map;
    }
}
