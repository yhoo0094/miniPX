package com.mpx.minipx.service.common;

import java.time.LocalDateTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.entity.TbToken;
import com.mpx.minipx.repository.TbTokenRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
//	protected static final Logger logger = LogManager.getLogger(ControllerAdvice.class);	
	
	private final TbTokenRepository tbTokenRepository;
	
    public void saveRefreshToken(String userId, String refreshTokenValue, long validityInMillis, String deviceInfo) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusSeconds(validityInMillis / 1000); // 밀리초 → 초

        TbToken token = TbToken.builder()
                .userId(userId)
                .token(refreshTokenValue)
                .createDtti(now)
                .expiryDtti(expiry)
                .deviceInfo(deviceInfo)
                .fstRegId(userId)
                .fstRegDtti(now)
                .lstUpdId(userId)
                .lstUpdDtti(now)
                .build();

        tbTokenRepository.save(token);
    } 
    
    
}
