package com.mpx.minipx.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mpx.minipx.dto.common.ReqLog;
import com.mpx.minipx.entity.TbLogReq;
import com.mpx.minipx.repository.TbLogReqRepository;

@Service
@RequiredArgsConstructor
public class ReqLogService {

    private final TbLogReqRepository tbLogReqRepository;

    public void save(ReqLog log) {
        try {
            TbLogReq e = new TbLogReq();
            e.setUserSeq(log.getUserSeq());
            e.setIp(log.getIp());
            e.setUri(log.getUri());
            e.setParam(log.getParam());
            e.setReqTypeCode(log.getReqTypeCode());

            tbLogReqRepository.save(e);
        } catch (Exception ex) {
            // 로그 저장 실패가 본 요청을 깨지 않도록
            // logger.warn("request log insert failed", ex);
        }
    }
}
