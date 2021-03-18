package com.baidu.shop.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName BrandeEntity
 * @Description: TODO
 * @Author wyj
 * @Date 2020/12/25
 * @Version V1.0
 **/
@Data
@ApiModel(value = "品牌实体类")
@Table(name = "tb_brand")
public class BrandeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String image;

    private Character letter;
}
