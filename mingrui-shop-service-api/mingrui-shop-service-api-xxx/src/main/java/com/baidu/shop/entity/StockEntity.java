package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName StockEntity
 * @Description: TODO
 * @Author yyq
 * @Date 2021/1/7
 * @Version V1.0
 **/
@Table(name = "tb_stock")
@Data
public class StockEntity {
    /*CREATE TABLE `tb_stock` (
            `sku_id` bigint(20) NOT NULL COMMENT '库存对应的商品sku id',
            `seckill_stock` int(9) DEFAULT '0' COMMENT '可秒杀库存',
            `seckill_total` int(9) DEFAULT '0' COMMENT '秒杀总数量',
            `stock` int(9) NOT NULL COMMENT '库存数量',
            PRIMARY KEY (`sku_id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='库存表，代表库存，秒杀库存等信息';*/

		@Id
		private Long skuId;

		private Integer seckillStock;

		private Integer seckillTotal;

		private Integer stock;
}
