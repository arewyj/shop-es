package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.fegin.GoodsFeign;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.CarService;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author wyj
 * @Date 2021/3/13
 * @Version V1.0
 **/
@RestController(value = "car")
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    private final String GOODS_CAR_PRE = "goods-car-";
    private final Integer CAR_KEY_INCREMENT = 1;

    @Autowired
    private RedisRepository redisRepository;

    @Resource
    private GoodsFeign goodsFeign;

    @Resource
    private JwtConfig jwtConfig;

    @Override
    public Result<JSONObject> operationNum(String token, Integer type, Long skuId) {

        try {
            UserInfo userInfor = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Car redisCar  = redisRepository.getHash(GOODS_CAR_PRE + userInfor.getId(), skuId + "", Car.class);
           /* if (type == 1){
                redisCar.setNum( redisCar.getNum() + 1);
            }else {
                redisCar.setNum( redisCar.getNum() - 1);
            }*/
            redisCar.setNum(type == CAR_KEY_INCREMENT ? redisCar.getNum() + 1: redisCar.getNum() -1 );

            redisRepository.setHash(GOODS_CAR_PRE + userInfor.getId(),skuId +"",JSONUtil.toJsonString(redisCar));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }




    @Override
    public Result<JSONObject> mergeCar(String carList, String token) {
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(carList);
        JSONArray carListJsonArr = jsonObject.getJSONArray("carLists");

        List<Car> carsList = carListJsonArr.toJavaList(Car.class);

        carsList.stream().forEach(car ->{
            this.addCar(car,token);
        });

        return this.setResultSuccess();
    }

    @Override
    public Result<List<Car>> getUserCar(String token) {
        List<Car> cars = new ArrayList<>();
        try {
            UserInfo userInfor = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Map<String,String > map = redisRepository.getHash(GOODS_CAR_PRE + userInfor.getId());

            map.forEach((key,value)-> cars.add(JSONUtil.toBean(value,Car.class)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess(cars);
    }



    @Override
    public Result<JSONObject> addCar(Car car, String token) {
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            Car redisCar  = redisRepository.getHash(GOODS_CAR_PRE + userInfo.getId(), car.getSkuId()+"", Car.class);
            log.info("通过用户id : {} ,skuId : {} 从redis中获取到的数据为 : {}",userInfo.getId(),car.getSkuId(),redisCar);

            if (ObjectUtil.isNotNull(redisCar)){
                //如果能查询出来数据 : num + num
                redisCar.setNum(redisCar.getNum() + car.getNum());
                redisRepository.setHash(GOODS_CAR_PRE + userInfo.getId(), car.getSkuId()+"", JSONUtil.toJsonString(redisCar));
                log.info("redis中有当前商品 , 重新设置redis中该商品的数量 : {}",redisCar.getNum());
            }else {
                Result<SkuEntity> skuResult = goodsFeign.getSkuById(car.getSkuId());
                if (skuResult.isSuccess()){

                    SkuEntity skuEntity = skuResult.getData();
                    car.setTitle(skuEntity.getTitle());
                    car.setImage(StringUtils.isEmpty(skuEntity.getImages()) ? "" : skuEntity.getImages().split(",")[0]);
                    car.setPrice(skuEntity.getPrice().longValue());
                    car.setOwnSpec(skuEntity.getOwnSpec());

                    redisRepository.setHash(GOODS_CAR_PRE + userInfo.getId(),car.getSkuId()+"",JSONUtil.toJsonString(car));
                    log.info("redis中没有当前商品 , 新增商品到购物车中 userId : {} , skuId : {} ,商品数据 : {}",userInfo.getId(),car.getSkuId(),JSONUtil.toJsonString(car));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  this.setResultSuccess();
    }
}
