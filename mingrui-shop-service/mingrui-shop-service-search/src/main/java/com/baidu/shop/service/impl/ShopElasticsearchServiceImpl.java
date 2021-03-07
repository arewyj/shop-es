package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandeEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.response.GoodsResponse;
import com.baidu.shop.service.ShopElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.HighlightUtil;
import com.baidu.shop.utils.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopElasticsearchServiceImpl
 * @Description: TODO
 * @Author yyq
 * @Date 2021/3/4
 * @Version V1.0
 **/
@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService implements ShopElasticsearchService {
    @Autowired
    private GoodsFeign goodsFeign;

    @Resource
    private SpecificationFeign specificationFeign;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private CategoryFeign categoryFeign;

    @Resource
    private BrandFeign brandFeign;

    @Override
    public Result<List<GoodsDoc>>   search(String search,Integer page) {

        SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(this.getNativeSearchQueryBuilder(search,page).build(), GoodsDoc.class);
        
        List<GoodsDoc> highlightList = HighlightUtil.getHighlightList(searchHits.getSearchHits());

        long total = searchHits.getTotalHits();//总条数
        long totalPage = Double.valueOf(Math.ceil(Double.valueOf(total) / 10)).longValue();//总页数

        Map<Integer,List<CategoryEntity>> map = this.getCategoryList(searchHits.getAggregations());

        Integer hotCid = 0;
        List<CategoryEntity> categoryList = null;
        for (Map.Entry<Integer,List<CategoryEntity>> entry : map.entrySet()){
            hotCid = entry.getKey();
            categoryList = entry.getValue();
        }

        return new GoodsResponse(total,totalPage,categoryList,
                this.getBrandList(searchHits.getAggregations()),highlightList,this.getSpecMap(search,hotCid));
    }

    private Map<String,List<String>>  getSpecMap(String search,Integer hotId){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotId);
        specParamDTO.setSearching(true);

        Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(specParamDTO);

        Map<String,List<String>> specMap = new HashMap<>();
        if (specParamInfo.isSuccess()){
            List<SpecParamEntity> specParamList  = specParamInfo.getData();
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

            nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));

            nativeSearchQueryBuilder.withPageable(PageRequest.of(0,1));

            specParamList.stream().forEach(specParam -> {
                nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs." + specParam.getName() + ".keyword"));
            });
            SearchHits<GoodsDoc> searchHits  = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), GoodsDoc.class);
            Aggregations aggregations = searchHits.getAggregations();

            specParamList.stream().forEach(specParam ->{
                Terms aggregation = aggregations.get(specParam.getName());
                List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
                List<String> valueList  = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

                specMap.put(specParam.getName(),valueList);
            });

        }
        return specMap;
    }


    private NativeSearchQueryBuilder getNativeSearchQueryBuilder(String search,Integer page){
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //多字段查询
        nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
        //设置分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page-1,10));
        //过滤掉无用的数据
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","title","skus"},null));
        //设置高亮
        nativeSearchQueryBuilder.withHighlightBuilder(HighlightUtil.getHighlightBuilder("title"));
        //聚合
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agg_category").field("cid3"));
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("agg_brand").field("brandId"));

        return nativeSearchQueryBuilder;
    }

    private Map<Integer,List<CategoryEntity>> getCategoryList(Aggregations aggregations){
        Terms agg_category = aggregations.get("agg_category");

        List<? extends Terms.Bucket> categoryBuckets = agg_category.getBuckets();

        List<Long> docCount  = Arrays.asList(0L);
        List<Integer> hotCid = Arrays.asList(0);

        List<String> categoryIdList = categoryBuckets.stream().map(categoryBucket -> {
            docCount.set(0, categoryBucket.getDocCount());
            hotCid.set(0, categoryBucket.getKeyAsNumber().intValue());

            return categoryBucket.getKeyAsNumber().longValue() + "";
        }).collect(Collectors.toList());

        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIdList(String.join(",",categoryIdList));

        List<CategoryEntity> categoryList = null;
        if (categoryResult.isSuccess()){
            categoryList = categoryResult.getData();
        }

        Map<Integer,List<CategoryEntity>> map = new HashMap<>();
        map.put(hotCid.get(0),categoryList);
        return map;
    }

    private List<BrandeEntity> getBrandList(Aggregations aggregations){
        Terms agg_brand = aggregations.get("agg_brand");

        List<? extends Terms.Bucket> brandBuckets = agg_brand.getBuckets();

        List<String> brandIdList = brandBuckets.stream().map(brandBucket -> brandBucket.getKeyAsNumber().longValue() + "").collect(Collectors.toList());

        Result<List<BrandeEntity>> brandResult = brandFeign.getBrandByIdList(String.join(",",brandIdList));

        List<BrandeEntity> brandList = null;
        if (brandResult.isSuccess()){
            brandList = brandResult.getData();
        }
        return brandList;
    }

    @Override
    public Result<JSONObject> initGoodsEsData() {

        IndexOperations operations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (!operations.exists()){
            operations.create();
            operations.createMapping();
        }
        List<GoodsDoc> goodsDocs = this.esGoodsInfo();
        elasticsearchRestTemplate.save(goodsDocs);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearGoodsEsData() {
        IndexOperations operations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if (operations.exists()){
            operations.delete();
        }
        return this.setResultSuccess();
    }

   // @Override
    public List<GoodsDoc> esGoodsInfo() {
        SpuDTO spuDTO = new SpuDTO();
        //spuDTO.setPage(1);
        //spuDTO.setRows(5);
        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);
        if (spuInfo.isSuccess()){
            List<SpuDTO> spuList = spuInfo.getData();
            List<GoodsDoc> goodsDocList = spuList.stream().map(spu -> {

                //spu数据
                GoodsDoc goodsDoc = new GoodsDoc();

                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());

                //sku数据  根据spuId查询sku数据
                Result<List<SkuDTO>> skusByspuId = goodsFeign.getSkusByspuId(spu.getId());
                if (skusByspuId.isSuccess()){
                    List<SkuDTO> skuList = skusByspuId.getData();
                    List<Long> priceList = new ArrayList<>();

                    List<Map<String, Object>> skuMapList = skuList.stream().map(sku -> {
                        //id,title,images,price
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", sku.getId());
                        map.put("title", sku.getTitle());
                        map.put("image", sku.getImages());
                        map.put("price", sku.getPrice());

                        priceList.add(sku.getPrice().longValue());
                        return map;
                    }).collect(Collectors.toList());

                    goodsDoc.setPrice(priceList);
                    goodsDoc.setSkus(JSONUtil.toJsonString(skuMapList));
                }
                //通过cid3查询规格参数
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spu.getCid3());
                specParamDTO.setSearching(true);
                Result<List<SpecParamEntity>> specParamInfo = specificationFeign.getSpecParamInfo(specParamDTO);
                if (specParamInfo.isSuccess()){
                    List<SpecParamEntity> specParamList = specParamInfo.getData();
                    Result<SpuDetailEntity> spuDetailByspuId = goodsFeign.getSpuDetailByspuId(spu.getId());
                    if (spuDetailByspuId.isSuccess()){
                        SpuDetailEntity spuDetailEntity = spuDetailByspuId.getData();
                        //将json字符串转换为map集合
                        Map<String, String> genericSpec = JSONUtil.toMapValueString(spuDetailEntity.getGenericSpec());
                        Map<String, List<String>> specialSpec = JSONUtil.toMapValueStrList(spuDetailEntity.getSpecialSpec());

                        Map<String, Object> specMap  = new HashMap<>();
                        specParamList.stream().forEach(specParamEntity -> {
                                if (specParamEntity.getGeneric()){
                                    if (specParamEntity.getNumeric() && !StringUtils.isEmpty(specParamEntity.getSegments())){
                                        specMap.put(specParamEntity.getName(),chooseSegment(genericSpec.get(specParamEntity.getId()+""),specParamEntity.getSegments(),specParamEntity.getUnit()));
                                    }else {
                                        specMap.put(specParamEntity.getName(),genericSpec.get(specParamEntity.getId()+""));
                                    }
                                }else {
                                    specMap.put(specParamEntity.getName(),specialSpec.get(specParamEntity.getId()+""));
                                }
                        });
                        goodsDoc.setSpecs(specMap);
                    }
                }
                return  goodsDoc;
            }).collect(Collectors.toList());
            //System.out.println(goodsDocList);
            return goodsDocList;
        }
        return null;
    }


    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }
}