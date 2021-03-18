package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName SkuEntity
 * @Description: TODO
 * @Author wyj
 * @Date 2021/1/7
 * @Version V1.0
 **/
@Table(name = "tb_sku")
@Data
public class SkuEntity {
    /*CREATE TABLE `tb_sku` (
            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'sku id',
            `spu_id` bigint(20) NOT NULL COMMENT 'spu id',
            `title` varchar(255) NOT NULL COMMENT '商品标题',
            `images` varchar(1000) DEFAULT '' COMMENT '商品的图片，多个图片以‘,’分割',
            `price` bigint(15) NOT NULL DEFAULT '0' COMMENT '销售价格，单位为分',
            `indexes` varchar(100) DEFAULT '' COMMENT '特有规格属性在spu属性模板中的对应下标组合',
            `own_spec` varchar(1000) DEFAULT '' COMMENT 'sku的特有规格参数键值对，json格式，反序列化时请使用linkedHashMap，保证有序',
            `enable` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效，0无效，1有效',
            `create_time` datetime NOT NULL COMMENT '添加时间',
            `last_update_time` datetime NOT NULL COMMENT '最后修改时间',
            PRIMARY KEY (`id`),
            KEY `key_spu_id` (`spu_id`) USING BTREE
        ) ENGINE=InnoDB AUTO_INCREMENT=27359021548 DEFAULT CHARSET=utf8 COMMENT='sku表,该表表示具体的商品实体,如黑色的 64g的iphone 8';*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer spuId;

    private String title;

    private String images;

    private Integer price;

    private String indexes;

    private String ownSpec;

    private Boolean enable;

    private Date createTime;

    private Date lastUpdateTime;

}
