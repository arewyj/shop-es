package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.CategoryService;
import com.baidu.shop.utils.ObjectUtil;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName CategoryServiceImpl
 * @Description: TODO
 * @Author yyq
 * @Date 2020/12/22
 * @Version V1.0
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByIdList(String ids) {
        List<Integer> idList = Arrays.asList(ids.split(",")).stream().map(idStr -> Integer.valueOf(idStr)).collect(Collectors.toList());
        List<CategoryEntity> categoryEntities = categoryMapper.selectByIdList(idList);
        return this.setResultSuccess(categoryEntities);
    }

    @Override
    public Result<List<CategoryEntity>> getCategoryByBrandId(Integer brandId) {
        List<CategoryEntity> list =  categoryMapper.getCategoryByBrandId(brandId);
        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JsonObject> saveCateGory(CategoryEntity categoryEntity) {

        CategoryEntity entity = new CategoryEntity();
        entity.setId(categoryEntity.getParentId());
        entity.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(entity);

        categoryMapper.insertSelective(categoryEntity);
        return this.setResultSuccess();
    }


    @Transactional
    @Override
    public Result<JsonObject> editCateGoryById(CategoryEntity entity) {

        categoryMapper.updateByPrimaryKeySelective(entity);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {
        CategoryEntity entity = new CategoryEntity();
        entity.setParentId(pid);
        List<CategoryEntity> list = categoryMapper.select(entity);
        return this.setResultSuccess(list);
    }

    @Transactional//事务注解  增删改都加这个注解
    @Override
    public Result<JsonObject> delCateGoryById(Integer id) {
        //判断Id是否合法
        if (ObjectUtil.isNull(id)) return this.setResultError("Id不合法");

        //根据Id查询数据
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

        //判断对象不为空
        if (ObjectUtil.isNull(categoryEntity)) return this.setResultError("数据不存在");

        //判断当前节点是否为父节点
        if (categoryEntity.getIsParent() == 1) return this.setResultError("当前节点为父节点");

        //根据分类ID查询中间表是否存在分类
        Example example1 = new Example(CategoryBrandEntity.class);
        example1.createCriteria().andEqualTo("categoryId",id);
        List<CategoryBrandEntity> categoryBrandEntities = categoryBrandMapper.selectByExample(example1);
        if(categoryBrandEntities.size() != 0){
            return this.setResultError("被绑定了不能删除");
        }

        //相当于拼接 Sql  通过被删除节点Id查询数据
        Example example = new Example(CategoryEntity.class);
        example.createCriteria().andEqualTo("parentId",categoryEntity.getParentId());
        List<CategoryEntity> categoryEntities = categoryMapper.selectByExample(example);

        if (categoryEntities.size() <= 1){

            CategoryEntity entity = new CategoryEntity();
            entity.setIsParent(0);
            entity.setId(categoryEntity.getParentId());

            categoryMapper.updateByPrimaryKeySelective(entity);
        }

        categoryMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

}
