package com.zzyl.nursing.service.impl;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.common.utils.bean.BeanUtils;
import com.zzyl.nursing.domain.NursingProject;
import com.zzyl.nursing.dto.NursingPlanDto;
import com.zzyl.nursing.mapper.NursingProjectPlanMapper;
import com.zzyl.nursing.vo.NursingPlanVo;
import com.zzyl.nursing.vo.NursingProjectPlanVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.mapper.NursingPlanMapper;
import com.zzyl.nursing.domain.NursingPlan;
import com.zzyl.nursing.service.INursingPlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 护理计划Service业务层处理
 * 
 * @author wtd
 * @date 2026-03-19
 */
@Service
public class NursingPlanServiceImpl extends ServiceImpl<NursingPlanMapper, NursingPlan> implements INursingPlanService
{
    @Autowired
    private NursingPlanMapper nursingPlanMapper;

    @Autowired
    private NursingProjectPlanMapper nursingProjectPlanMapper;

    /**
     * 查询护理计划
     * 
     * @param id 护理计划主键
     * @return 护理计划
     */
    @Override
    public NursingPlanVo selectNursingPlanById(Long id)
    {
        //查询护理计划基本信息
        NursingPlan nursingPlan = nursingPlanMapper.selectNursingPlanById(id);

        //查询护理计划项目列表

        List<NursingProjectPlanVo> projectPlans = nursingProjectPlanMapper.selectByNursingPlanId(id);

        //两部分合并到一起返回
        NursingPlanVo nursingPlanVo = new NursingPlanVo() ;
        BeanUtils.copyProperties(nursingPlan,nursingPlanVo);
        nursingPlanVo.setProjectPlans(projectPlans);

        return nursingPlanVo;
    }

    /**
     * 查询护理计划列表
     * 
     * @param nursingPlan 护理计划
     * @return 护理计划
     */
    @Override
    public List<NursingPlan> selectNursingPlanList(NursingPlan nursingPlan)
    {
        return nursingPlanMapper.selectNursingPlanList(nursingPlan);
    }

    /**
     * 新增护理计划
     * 
     * @param dto 护理计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNursingPlan(NursingPlanDto dto)
    {
        NursingPlan nursingPlan = new NursingPlan() ;
        BeanUtils.copyProperties(dto,nursingPlan);
        nursingPlan.setCreateTime(DateUtils.getNowDate());

        nursingPlanMapper.insert(nursingPlan) ;

        //批量插入护理计划
        int count=nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(),nursingPlan.getId());

        return count==0 ? 0 : 1;
    }

    /**
     * 修改护理计划
     * 
     * @param dto 护理计划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateNursingPlan(NursingPlanDto dto)
    {
        //修改护理计划基本信息
        if(dto.getProjectPlans()!= null|| !dto.getProjectPlans().isEmpty()){
            //删除护理计划项目列表
            nursingProjectPlanMapper.deleteByNursingPlanId(dto.getId());
            //再保存护理计划项目列表
            nursingProjectPlanMapper.batchInsert(dto.getProjectPlans(),dto.getId());
        }

        //修改护理计划项目列表
        NursingPlan nursingPlan = new NursingPlan() ;
        BeanUtils.copyProperties(dto,nursingPlan);
        return nursingPlanMapper.updateById(nursingPlan);
    }

    /**
     * 批量删除护理计划
     * 
     * @param ids 需要删除的护理计划主键
     * @return 结果
     */
    @Override
    public int deleteNursingPlanByIds(Long[] ids)
    {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除护理计划信息
     * 
     * @param id 护理计划主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteNursingPlanById(Long id)
    {
        //删除护理计划对应的护理项目
        nursingProjectPlanMapper.deleteByNursingPlanId(id);
        //删除护理计划
        return removeById(id) ? 1 : 0;
    }

    /**
     * 获取所有护理计划
     *
     * @return
     */
    @Override
    public List<NursingPlan> getAllNursingPlans() {
        LambdaQueryWrapper<NursingPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NursingPlan::getStatus, 1);
        return list(queryWrapper);
    }
}
