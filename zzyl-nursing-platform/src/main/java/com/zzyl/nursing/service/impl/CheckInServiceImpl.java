package com.zzyl.nursing.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzyl.common.exception.base.BaseException;
import com.zzyl.common.utils.CodeGenerator;
import com.zzyl.common.utils.DateUtils;
import com.zzyl.nursing.domain.*;
import com.zzyl.nursing.dto.CheckInApplyDto;
import com.zzyl.nursing.dto.CheckInElderDto;
import com.zzyl.nursing.mapper.*;
import com.zzyl.nursing.vo.CheckInConfigVo;
import com.zzyl.nursing.vo.CheckInDetailVo;
import com.zzyl.nursing.vo.CheckInElderVo;
import com.zzyl.nursing.vo.ElderFamilyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.zzyl.nursing.service.ICheckInService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

/**
 * 入住Service业务层处理
 * 
 * @author alexis
 * @date 2026-03-23
 */
@Service
public class CheckInServiceImpl extends ServiceImpl<CheckInMapper, CheckIn> implements ICheckInService {
    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private ElderMapper elderMapper;

    @Autowired
    private BedMapper bedMapper;

    @Autowired
    private ContractMapper contractMapper;

    @Autowired
    private CheckInConfigMapper checkInConfigMapper;

    /**
     * 查询入住
     *
     * @param id 入住主键
     * @return 入住
     */
    @Override
    public CheckIn selectCheckInById(Long id) {
        return getById(id);
    }

    /**
     * 查询入住列表
     *
     * @param checkIn 入住
     * @return 入住
     */
    @Override
    public List<CheckIn> selectCheckInList(CheckIn checkIn) {
        return checkInMapper.selectCheckInList(checkIn);
    }

    /**
     * 新增入住
     *
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int insertCheckIn(CheckIn checkIn) {
        return save(checkIn) ? 1 : 0;
    }

    /**
     * 修改入住
     *
     * @param checkIn 入住
     * @return 结果
     */
    @Override
    public int updateCheckIn(CheckIn checkIn) {
        return updateById(checkIn) ? 1 : 0;
    }

    /**
     * 批量删除入住
     *
     * @param ids 需要删除的入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInByIds(Long[] ids) {
        return removeByIds(Arrays.asList(ids)) ? 1 : 0;
    }

    /**
     * 删除入住信息
     *
     * @param id 入住主键
     * @return 结果
     */
    @Override
    public int deleteCheckInById(Long id) {
        return removeById(id) ? 1 : 0;
    }

    /**
     * 申请入住
     *
     * @param checkInApplyDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void apply(CheckInApplyDto checkInApplyDto) {
        // 校验老人是否入住
        LambdaQueryWrapper<Elder> elderQueryWrapper = new LambdaQueryWrapper<>();
        elderQueryWrapper.eq(Elder::getIdCardNo, checkInApplyDto.getCheckInElderDto().getIdCardNo())
                .in(Elder::getStatus, 1, 4);
        Elder elder = elderMapper.selectOne(elderQueryWrapper);
        if (ObjectUtil.isNotEmpty(elder)) {
            throw new BaseException("该老人已入住,不能重复入住");
        }
        //更新床位状态为已入住
        Bed bed = bedMapper.selectById(checkInApplyDto.getCheckInConfigDto().getBedId());
        bed.setBedStatus(1);
        bedMapper.updateById(bed);
        //新增或更新老人基本信息
        elder =insertOrUpdateElder(bed, checkInApplyDto.getCheckInElderDto());

        //生成一个合同编号
        String contractNumber = "HT" + CodeGenerator.generateContractNumber();

        //新增签约办理
        insertContract(contractNumber, checkInApplyDto, elder);

        //新增入住信息
        CheckIn checkIn = insertCheckInInfo(elder,checkInApplyDto);

        //新增入住配置
        insertCheckInConfig(checkIn.getId(),checkInApplyDto);
    }

    /**
     * 获取入住详情
     *
     * @param id
     * @return
     */
    @Override
    public CheckInDetailVo detail(Long id) {
        CheckInDetailVo checkInDetailVo = new CheckInDetailVo();

        //获取入住设置响应信息
        CheckIn checkIn = checkInMapper.selectById(id);
        CheckInConfigVo checkInConfigVo = new CheckInConfigVo();
        BeanUtils.copyProperties(checkIn, checkInConfigVo);
        CheckInConfig checkInConfig = checkInConfigMapper.selectOne(new LambdaQueryWrapper<CheckInConfig>().eq(CheckInConfig::getCheckInId, id));
        BeanUtils.copyProperties(checkInConfig, checkInConfigVo);
        // 设置入住配置信息
        checkInDetailVo.setCheckInConfigVo(checkInConfigVo);


        //获取老人响应信息
        CheckInElderVo checkInElderVo = new CheckInElderVo();
        Long elderId = checkIn.getElderId();
        Elder elder = elderMapper.selectById(elderId);
        BeanUtils.copyProperties(elder, checkInElderVo);

        checkInElderVo.setAge(IdcardUtil.getAgeByIdCard(elder.getIdCardNo()));
        checkInDetailVo.setCheckInElderVo(checkInElderVo);

        //获取家属响应信息
        String remark = checkIn.getRemark();
        List<ElderFamilyVo> elderFamilyVoList = JSON.parseArray(remark, ElderFamilyVo.class);
        checkInDetailVo.setElderFamilyVoList(elderFamilyVoList);

        //获取签约办理响应信息
        Contract contract = contractMapper.selectOne(new LambdaQueryWrapper<Contract>().eq(Contract::getElderId, elderId));
        checkInDetailVo.setContract(contract);

        return checkInDetailVo;
    }
    /**
     * 新增入住配置
     * @param id 入住ID键
     * @param checkInApplyDto
     */
    private void insertCheckInConfig(Long id, CheckInApplyDto checkInApplyDto) {
        CheckInConfig checkInConfig = new CheckInConfig();
        checkInConfig.setCheckInId(id);
        BeanUtils.copyProperties(checkInApplyDto.getCheckInConfigDto(), checkInConfig);
        checkInConfigMapper.insert(checkInConfig);
    }

    /**
     * 新增入住信息
     * @param elder
     * @param checkInApplyDto
     */
    private CheckIn insertCheckInInfo(Elder elder, CheckInApplyDto checkInApplyDto) {
            CheckIn checkIn = new CheckIn();
            checkIn.setElderId(elder.getId());
            checkIn.setElderName(elder.getName());
            checkIn.setIdCardNo(elder.getIdCardNo());
            checkIn.setStartDate(checkInApplyDto.getCheckInConfigDto().getStartDate());
            checkIn.setEndDate(checkInApplyDto.getCheckInConfigDto().getEndDate());
            checkIn.setNursingLevelName(checkInApplyDto.getCheckInConfigDto().getNursingLevelName());
            checkIn.setBedNumber(elder.getBedNumber());
            checkIn.setStatus(0);
            checkIn.setRemark(JSON.toJSONString(checkInApplyDto.getElderFamilyDtoList()));
            checkInMapper.insert(checkIn);
            return checkIn;
    }

    /**
     * 新增或更新老人基本信息
     * @param checkInApplyDto
     * @param contractNumber
     * @param elder
     * @return
     */
    private void insertContract(String contractNumber, CheckInApplyDto checkInApplyDto, Elder elder) {
        //新增签约办理
        Contract contract = new Contract();
        BeanUtils.copyProperties(checkInApplyDto.getCheckInContractDto(), contract);
        contract.setContractNumber(contractNumber);
        contract.setElderId(elder.getId());
        contract.setElderName(elder.getName());
        LocalDateTime startDate = checkInApplyDto.getCheckInConfigDto().getStartDate();
        contract.setStartDate(startDate);
        LocalDateTime endDate = checkInApplyDto.getCheckInConfigDto().getEndDate();
        contract.setEndDate(endDate);

        //设置合同状态
        int status=startDate.isAfter(LocalDateTime.now())?0:1;
        contract.setStatus(status);

        contractMapper.insert(contract);
    }

    /**
     * 新增或更新老人信息
     *
     * @param bed
     * @param checkInElderDto
     */
    private Elder insertOrUpdateElder(Bed bed, CheckInElderDto checkInElderDto) {
        //转呗一个elder对象
        Elder elder = new Elder();

        BeanUtils.copyProperties(checkInElderDto, elder);
        elder.setBedId(bed.getId());
        elder.setBedNumber(bed.getBedNumber());
        elder.setStatus(1);
        //判断是否新增老人
        LambdaQueryWrapper<Elder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Elder::getIdCardNo, elder.getIdCardNo());
        queryWrapper.notIn(Elder::getStatus, 1, 4);
        Elder elderInDb = elderMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(elderInDb)) {
            //更新老人信息
            elder.setId(elderInDb.getId());
            elderMapper.updateById(elder);
        }else{
            elderMapper.insert(elder);
        }
        return elder;
        }
}