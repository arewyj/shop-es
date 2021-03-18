package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName SpuEntity
 * @Description: TODO
 * @Author wyj
 * @Date 2021/1/5
 * @Version V1.0
 **/
@Table(name = "tb_spu")
@Data
public class SpuEntity {

             /*CREATE TABLE `tb_spu` (
            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'spu id',
            `title` varchar(255) NOT NULL DEFAULT '' COMMENT '标题',
            `sub_title` varchar(255) DEFAULT '' COMMENT '子标题',
            `cid1` bigint(20) NOT NULL COMMENT '1级类目id',
            `cid2` bigint(20) NOT NULL COMMENT '2级类目id',
            `cid3` bigint(20) NOT NULL COMMENT '3级类目id',
            `brand_id` bigint(20) NOT NULL COMMENT '商品所属品牌id',
            `saleable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否上架，0下架，1上架',
            `valid` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效，0已删除，1有效',
            `create_time` datetime DEFAULT NULL COMMENT '添加时间',
            `last_update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
             PRIMARY KEY (`id`)
             ) ENGINE=InnoDB AUTO_INCREMENT=234 DEFAULT CHARSET=utf8 COMMENT='spu表，该表描述的是一个抽象性的商品，比如 iphone8';*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String subTitle;

    private Integer cid1;

    private Integer cid2;

    private Integer cid3;

    private Integer brandId;

    private Integer saleable;

    private Integer valid;

    private Date createTime;

    private Date lastUpdateTime;
}
