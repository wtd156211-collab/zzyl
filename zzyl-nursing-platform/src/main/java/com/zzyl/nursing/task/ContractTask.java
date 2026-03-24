package com.zzyl.nursing.task;

import com.zzyl.nursing.service.IContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ContractTask {
    @Autowired
    private IContractService icontractService;
    public void updateContractStatus(){
        log.info("更新合同状态开始:{}", LocalDateTime.now());
        icontractService.updateContractStatus();
        log.info("更新合同状态结束:{}", LocalDateTime.now());
    }
}
