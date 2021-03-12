package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.constant.MqMessageConstant;
import com.baidu.shop.constant.MrRabbitMQ;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.SpuDetailDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author yyq
 * @Date 2021/1/5
 * @Version V1.0
 **/
@RestController
@Slf4j
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private BrandMapper brandMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private MrRabbitMQ mrRabbitMQ;

    @Override
    @Transactional
    public Result<JSONObject> updateSaleable(SpuDTO spuDTO) {
        SpuEntity spuEntity = BaiduBeanUtils.copyProperties(spuDTO, SpuEntity.class);
        if (ObjectUtil.isNotNull(spuEntity.getSaleable()) && spuDTO.getSaleable() < 2){
            if (spuEntity.getSaleable() == 1){
                spuEntity.setSaleable(0);
            }else {
                spuEntity.setSaleable(1);
            }
            spuMapper.updateByPrimaryKeySelective(spuEntity);
        }

        return this.setResultSuccess();
    }

    @Override
    @Transactional
    public Result<JSONObject> delete(Integer spuId) {
        //删除spu信息
        spuMapper.deleteByPrimaryKey(spuId);
        //删除spuDetail信息
        spuDetailMapper.deleteByPrimaryKey(spuId);
        //删除sku和stock信息
        this.deleteSkuAndStockInfo(spuId);

       // mrRabbitMQ.send(spuId+"", MqMessageConstant.SPU_ROUT_KEY_DELETE);
        return this.setResultSuccess();
    }

    //封装删除sku和stock信息
    private void deleteSkuAndStockInfo(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);

        List<Long> skuIdList = skuEntities.stream().map(skuEntity ->
                skuEntity.getId()
        ).collect(Collectors.toList());
        stockMapper.deleteByIdList(skuIdList);
        skuMapper.deleteByIdList(skuIdList);

    }

    @Override
    @Transactional
    public Result<JSONObject> updateGoods(SpuDTO spuDTO) {
        final Date date = new Date();
        //修改spu
        SpuEntity spuEntity = BaiduBeanUtils.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //修改spuDetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtils.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailMapper.updateByPrimaryKeySelective(spuDetailEntity);

        //根据spuId查询sku信息
        this.deleteSkuAndStockInfo(spuDTO.getId());
        //删除sku信息后   进行批量新增
        //新增sku
        this.saveSkuAndStockInfo(spuDTO,spuEntity.getId(),date);

        mrRabbitMQ.send(spuDTO.getId()+"", MqMessageConstant.SPU_ROUT_KEY_UPDATE);
        return this.setResultSuccess();
    }

    //封装新增sku和stock方法啊
    private void saveSkuAndStockInfo(SpuDTO spuDTO,Integer spuId,Date date){
        List<SkuDTO> skus  = spuDTO.getSkus();
        skus.stream().forEach(skuDTO -> {
            SkuEntity skuEntity = BaiduBeanUtils.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增tock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }

    @Override
    public Result<List<SkuDTO>> getSkusByspuId(Integer spuId) {
        List<SkuDTO> list = skuMapper.getSkusAndStockByspuId(spuId);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetailByspuId(Integer spuId) {
        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }

    @Override
    //@Transactional
    public Result<JSONObject> saveGoods(SpuDTO spuDTO) {

        Integer spuId = this.saveGoodsTransactional(spuDTO);
        mrRabbitMQ.send(spuId + "", MqMessageConstant.SPU_ROUT_KEY_SAVE);
        return this.setResultSuccess();
    }

    @Transactional
    public Integer saveGoodsTransactional(SpuDTO spuDTO){
        //final finally finalize()的区别????
        final Date date = new Date();
        //新增spu,新增返回主键, 给必要字段赋默认值
        SpuEntity spuEntity = BaiduBeanUtils.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);

        //新增spuDetail
        SpuDetailDTO spuDetail = spuDTO.getSpuDetail();
        SpuDetailEntity spuDetailEntity = BaiduBeanUtils.copyProperties(spuDetail, SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);

        //新增sku list插入顺序有序 b,a set a,b treeSet b,a
        this.saveSkuAndStockInfo(spuDTO,spuEntity.getId(),date);
        return spuEntity.getId();
    }

    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {
        if (ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows()))
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        if (!StringUtils.isEmpty(spuDTO.getSort()) && !StringUtils.isEmpty(spuDTO.getOrder()))
            PageHelper.orderBy(spuDTO.getOrderBy());

        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() < 2)
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        if (!StringUtils.isEmpty(spuDTO.getTitle()))
            criteria.andLike("title","%"+spuDTO.getTitle()+"%");
        if (ObjectUtil.isNotNull(spuDTO.getId()) && spuDTO.getId() > 0)
            criteria.andEqualTo("id",spuDTO.getId());

        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);

        List<SpuDTO> spuDTOList = spuEntities.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BaiduBeanUtils.copyProperties(spuEntity, SpuDTO.class);
            //根据cid1,cid2,cid3查询分类名称
            /*CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(spuDTO1.getCid1());
            CategoryEntity categoryEntity2 = categoryMapper.selectByPrimaryKey(spuDTO1.getCid2());
            CategoryEntity categoryEntity3 = categoryMapper.selectByPrimaryKey(spuDTO1.getCid3());
            spuDTO1.setCategoryName(categoryEntity.getName() + "/" + categoryEntity2.getName() + "/" + categoryEntity3.getName());*/

            List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(Arrays.asList(spuEntity.getCid1(), spuEntity.getCid2(), spuEntity.getCid3()));
            String categoryName = categoryEntities.stream().map(categoryEntity -> categoryEntity.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(categoryName);

           /* String categoryName = "";
            List<String> strings = new ArrayList<>();
            strings.add(0,"");
            categoryEntities.stream().forEach(categoryEntity -> {
                strings.add(0,strings.get(0) + categoryEntity.getName() + "/");
            });
            categoryName = strings.get(0).substring(0,strings.get(0).length());*/

            //根据brandId查询品牌名称
            BrandeEntity brandeEntity = brandMapper.selectByPrimaryKey(spuDTO1.getBrandId());
            spuDTO1.setBrandName(brandeEntity.getName());
            return spuDTO1;
        }).collect(Collectors.toList());

        PageInfo<SpuEntity> spuEntityPageInfo = new PageInfo<>(spuEntities);
       // return this.setResultSuccess(spuEntityPageInfo);
        return this.setResult(HTTPStatus.OK,spuEntityPageInfo.getTotal()+"",spuDTOList);
    }
}
