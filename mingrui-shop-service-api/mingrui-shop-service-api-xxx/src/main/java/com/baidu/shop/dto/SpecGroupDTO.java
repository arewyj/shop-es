package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName SpecGroupDTO
 * @Description: TODO
 * @Author wyj
 * @Date 2021/1/4
 * @Version V1.0
 **/
@ApiModel(value = "规格组数据输出DTO")
@Data
public class SpecGroupDTO extends BaseDTO {


    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "分类Id",example = "1")
    @NotNull(message = "分类Id不能为空",groups = {MingruiOperation.Update.class})
    private Integer cid;

    @ApiModelProperty(value = "规格组名称")
    @NotEmpty(message = "规格名称不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String name;

    private List<SpecParamEntity> specList;
}
