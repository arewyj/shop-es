package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandeEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author wyj
 * @Date 2020/12/25
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<List<BrandeEntity>> getBrandByIdList(String ids) {
        List<Integer> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Integer.valueOf(idStr)).collect(Collectors.toList());
        List<BrandeEntity> brandeEntities = brandMapper.selectByIdList(idList);
        return this.setResultSuccess(brandeEntities);
    }

    @Override
    public Result<List<BrandeEntity>> getBrandByCategoryId(Integer cid) {
       List<BrandeEntity> list =  brandMapper.getBrandByCategoryId(cid);
        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> deleteBrandInfo(Integer id) {
        //删除品牌信息
        brandMapper.deleteByPrimaryKey(id);
        //删除根据品牌Id查询出来的分类信息
        this.deleteCategoryBrandById(id);

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> updateBrandInfo(BrandDTO brandDTO) {

        BrandeEntity brandeEntity = BaiduBeanUtils.copyProperties(brandDTO, BrandeEntity.class);
        brandeEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandeEntity.getName().toCharArray()[0]), false).toCharArray()[0]);
        brandMapper.updateByPrimaryKeySelective(brandeEntity);

        //先通过brandId删除中间表数据
        this.deleteCategoryBrandById(brandDTO.getId());

        this.insertCategoryBrandList(brandDTO.getCategories(),brandeEntity.getId());

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> saveBrandInfo(BrandDTO brandDTO) {

        BrandeEntity brandeEntity = BaiduBeanUtils.copyProperties(brandDTO, BrandeEntity.class);
        brandeEntity.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandeEntity.getName().toCharArray()[0]), false).toCharArray()[0]);
        brandMapper.insertSelective(brandeEntity);

        this.insertCategoryBrandList(brandDTO.getCategories(),brandeEntity.getId());

        return this.setResultSuccess();
    }

    @Override
    public Result<PageInfo<BrandeEntity>> getBrandInfo(BrandDTO brandDTO) {

        if(ObjectUtil.isNotNull(brandDTO.getPage()) && ObjectUtil.isNotNull(brandDTO.getRows()))
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        //当sort不为空的情况下
        if (!StringUtils.isEmpty(brandDTO.getSort())) {
            //SELECT * FROM 表名  ORDER BY id desc
            PageHelper.orderBy(brandDTO.getSort() + " " + (Boolean.valueOf(brandDTO.getOrder()) ? "desc" : "asc"));
        }
        //条件查询
        BrandeEntity brandeEntity = BaiduBeanUtils.copyProperties(brandDTO, BrandeEntity.class);

        //创建Example的实例
        Example example = new Example(BrandeEntity.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(brandeEntity.getName()))
            criteria.andLike("name", "%" + brandeEntity.getName() + "%");
        if(ObjectUtil.isNotNull(brandDTO.getId()))
            criteria.andEqualTo("id",brandDTO.getId());

        List<BrandeEntity> list = brandMapper.selectByExample(example);

        PageInfo<BrandeEntity> pageInfo = new PageInfo<>(list);
        return this.setResultSuccess(pageInfo);
    }

    private void deleteCategoryBrandById(Integer brandId){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",brandId);
        categoryBrandMapper.deleteByExample(example);
    }

    private void insertCategoryBrandList(String categories,Integer brandId){
        if (StringUtils.isEmpty(categories)) throw  new RuntimeException("商品分类信息不能为空");

        if (categories.contains(",")) {
            categoryBrandMapper.insertList(Arrays.asList(categories.split(","))
                            .stream()
                            .map(categoryIdStr -> new CategoryBrandEntity(Integer.valueOf(categoryIdStr), brandId))
                            .collect(Collectors.toList())
            );
        } else {
            CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity();
            categoryBrandEntity.setBrandId(brandId);
            categoryBrandEntity.setCategoryId(Integer.valueOf(categories));
            categoryBrandMapper.insertSelective(categoryBrandEntity);
        }
    }
}
