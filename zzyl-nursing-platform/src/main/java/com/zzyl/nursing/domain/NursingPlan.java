package com.zzyl.nursing.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 护理计划对象 nursing_plan
 * 
 * @author wtd
 * @date 2026-03-19
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "护理计划对象", description = "护理计划对象")
public class NursingPlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @ApiModelProperty(value = "编号")
    private Long id;

    /** 排序号 */
    @Excel(name = "排序号")
    @ApiModelProperty(value = "排序号")
    private Integer sortNo;

    /** 名称 */
    @Excel(name = "名称")
    @ApiModelProperty(value = "名称")
    private String planName;

    /** 状态 0禁用 1启用 */
    @Excel(name = "状态 0禁用 1启用")
    @ApiModelProperty(value = "状态 0禁用 1启用")
    private Integer status;


}
