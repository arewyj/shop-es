package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName SpecParamEntity
 * @Description: TODO
 * @Author wyj
 * @Date 2021/1/4
 * @Version V1.0
 **/
@Table(name = "tb_spec_param")
@Data
public class SpecParamEntity {

           /* `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
            `cid` bigint(20) NOT NULL COMMENT '商品分类id',
            `group_id` bigint(20) NOT NULL,
             `name` varchar(255) NOT NULL COMMENT '参数名',
            `numeric` tinyint(1) NOT NULL COMMENT '是否是数字类型参数，true或false',
            `unit` varchar(255) DEFAULT '' COMMENT '数字类型参数的单位，非数字类型可以为空',
            `generic` tinyint(1) NOT NULL COMMENT '是否是sku通用属性，true或false',
            `searching` tinyint(1) NOT NULL COMMENT '是否用于搜索过滤，true或false',
            `segments` varchar(1000) DEFAULT '' COMMENT '数值类型参数，如果需要搜索，则添加分段间隔值，如CPU频率间隔：0.5-1.0',*/
        @Id
        private Integer id;

        private Integer cid;

        private Integer groupId;

        private String name;

        @Column(name = "`numeric`")
        private Boolean numeric;

        private String unit;

        private Boolean generic;

        private Boolean searching;

        private String segments;
}
