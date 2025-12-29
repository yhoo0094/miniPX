package com.mpx.minipx.service.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mpx.minipx.controller.common.BaseController;
import com.mpx.minipx.dto.user.UserInfoDto;
import com.mpx.minipx.entity.TbLogLogin;
import com.mpx.minipx.entity.TbUser;
import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.repository.RedisPermissionRepository;
import com.mpx.minipx.repository.TbLogLoginRepository;
import com.mpx.minipx.repository.TbTokenRepository;
//import com.mpx.minipx.framework.aop.ControllerAdvice;
import com.mpx.minipx.repository.TbUserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
	
	private final AuthService authService;
	
    @Autowired
    private SqlSessionTemplate sqlSession;    
    
    @Autowired
    private TbUserRepository tbUserRepository;
    
    @Autowired
    private TbLogLoginRepository tbLogLoginRepository;
    
    @Autowired
    private TbTokenRepository tbTokenRepository;        
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;   
    
    @Autowired
    private RedisPermissionRepository redisPermissionRepository;      
    
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)    
    
    /**
     * @메소드명: logout
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 3.
     * @설명: 로그아웃
     */
    @Transactional
    public Map<String, Object> logout(String userId, HttpServletRequest request) {
    	Map<String, Object> result = new HashMap<>();

        //ip구하기
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        else ip = ip.split(",")[0].trim();    
        
        // 사용자 조회
        TbUser user = tbUserRepository.findByUserId(userId);         
    	
        //로그인 정보 저장
        saveLoginLog(user, userId, ip, "02");
        
    	tbTokenRepository.deleteByUserId(userId);
    	return result;
    }    
    
    /**
     * @메소드명: login
     * @작성자: KimSangMin
     * @생성일: 2025. 6. 19.
     * @설명: 로그인
     */
    public Map<String, Object> login(Map<String, Object> inData, HttpServletRequest request) {
    	Map<String, Object> result = new HashMap<>();
    	
        String userId = (String) inData.get("userId");
        String userPw = (String) inData.get("userPw");
        
        //ip구하기
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        else ip = ip.split(",")[0].trim();        
        
        // 사용자 조회
        TbUser user = tbUserRepository.findByUserId(userId);   
        if (user == null) {
        	saveLoginLog(null, userId, ip, "03");
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "존재하지 않는 사용자입니다.");
            return result;
        }
        
        //오입력 횟수 초과
        if (5 <= user.getPwErrCnt()) {
        	saveLoginLog(user, userId, ip, "05");
        	result.put(Constant.RESULT, Constant.RESULT_FAILURE);
        	result.put(Constant.OUT_RESULT_MSG, "비밀번호 오입력 횟수를 초과하였습니다. 관리자에게 문의하세요.");
        	return result;
        }   
        
        // BCrypt로 비밀번호 비교
        if (!passwordEncoder.matches(userPw, user.getUserPw())) {
        	saveLoginLog(user, userId, ip, "04");
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
            result.put(Constant.OUT_RESULT_MSG, "비밀번호가 일치하지 않습니다.");  
            
            user.increasePwErrCnt();
            tbUserRepository.save(user);
            return result;
        }
        
        //로그인 정보 저장
        saveLoginLog(user, userId, ip, "01");
        
        //사용자 정보 저장
        UserInfoDto userInfo = new UserInfoDto();
        userInfo.setUserSeq(user.getUserSeq());
        userInfo.setUserId(user.getUserId());
        userInfo.setUserNm(user.getUserNm());
        userInfo.setAiOpenYn(user.getAiOpenYn());
        userInfo.setCredit(user.getCredit());
        userInfo.setUseYn(user.getUseYn());
        userInfo.setRoleSeq(user.getRoleSeq());
        userInfo.setPwInitYn(user.getPwInitYn());
        result.put("userInfo", userInfo);

        // accessToken 생성
        String accessToken = JwtUtil.generateToken(user, jwtSecret, Constant.ACCESS_TOKEN_VALIDITY);
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
//        accessCookie.setSecure(true);		//HTTPS에서만 전송(주석 풀어야함)
        accessCookie.setSecure(false);		//개발용도
        accessCookie.setMaxAge((int)Constant.ACCESS_TOKEN_VALIDITY);
        accessCookie.setPath("/");   
        result.put("accessToken", accessCookie);	//컨트롤러에서 response에 추가해야함
        
        // refreshToken 생성
        String refreshToken = JwtUtil.generateToken(user, jwtSecret, Constant.REFRESH_TOKEN_VALIDITY);
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);  // JavaScript에서 쿠키에 접근 불가
//        refreshCookie.setSecure(true);    //HTTPS에서만 전송(주석 풀어야함)     
        refreshCookie.setSecure(false);      //개발용도                     
        refreshCookie.setMaxAge((int)Constant.REFRESH_TOKEN_VALIDITY);
        refreshCookie.setPath("/");       // 전체 경로에 대해 유효 
        result.put("refreshToken", refreshCookie);	//컨트롤러에서 response에 추가해야함
        
        // refreshToken을 DB에 저장
        authService.saveRefreshToken(
    	    user.getUserId(),
    	    user.getUserSeq(),
    	    refreshToken,                     // JWT 문자열
    	    Constant.REFRESH_TOKEN_VALIDITY,  
    	    (String) inData.get("remoteAddr") // 사용자 기기 정보
    	);
        
        // 메뉴 목록 
        inData.put("roleSeq", user.getRoleSeq());
        List<Map<String, Object>> mnuList = sqlSession.selectList("com.mpx.minipx.mapper.UserMapper.getUserMnuList", inData);
        result.put("mnuList", mnuList);
        
        // Redis에 권한 저장
        for (Map<String, Object> menu : mnuList) {
            String url = String.valueOf(menu.get("url"));          // URL 컬럼
            Integer authGrade = Integer.parseInt(String.valueOf(menu.get("authGrade"))); // 권한 (0: 없음, 1: 읽기, 2: 쓰기)

            if (url != null && authGrade != null) {
                redisPermissionRepository.savePermission(userId, url, authGrade);
            }
        }
    	
        result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        result.put(Constant.OUT_RESULT_MSG, "로그인 성공");
    	
    	return result;
    }    
    
    /**
     * @메소드명: changePassword
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 23.
     * @설명: 비밀번호 변경
     */    
    public Map<String, Object> changePassword(Map<String, Object> inData) {
        Map<String, Object> result = new HashMap<>();
        
        // 사용자 조회
        TbUser user = tbUserRepository.findByUserId((String)inData.get("userId"));   
        if (user == null) {
            result.put(Constant.RESULT, false);
            result.put("message", "존재하지 않는 사용자입니다.");
            return result;
        }
        
        // BCrypt로 비밀번호 비교
        String userPw = user.getUserPw();
        if (!passwordEncoder.matches((String)inData.get("currentPassword"), userPw)) {
            result.put(Constant.RESULT, false);
            result.put("message", "비밀번호가 일치하지 않습니다.");  
            return result;
        }        

        String encodedNewPassword = passwordEncoder.encode((String)inData.get("newPassword"));
        
        inData.put("encodedNewPassword", encodedNewPassword);
        inData.put("userPw", userPw);
        int affected = sqlSession.update("com.mpx.minipx.mapper.UserMapper.changePassword", inData);

        if (affected > 0) {
            result.put(Constant.OUT_DATA, affected);
            result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
        } else {
            result.put(Constant.RESULT, Constant.RESULT_FAILURE);
        }
        return result;
    }       
    
    /**
     * @메소드명: saveLoginLog
     * @작성자: KimSangMin
     * @생성일: 2025. 12. 29.
     * @설명: 로그인 로그 저장
     */
    private void saveLoginLog(TbUser user, String userId, String ip, String loginCode) {
        TbLogLogin log = new TbLogLogin();
        log.setLoginDtti(LocalDateTime.now());
        log.setUserId(userId);
        log.setIp(ip);
        log.setLoginCode(loginCode);	//01: 로그인, 02: 로그아웃, 03: 존재하지 않는 아이디, 04: 비밀번호 오입력, 05: 비밀번호 오입력 횟수 초과 

        // 사용자 존재 여부에 따라 저장값 세팅
        if (user != null) {
            try {
                log.setUserSeq(Long.parseLong(String.valueOf(user.getUserSeq())));
            } catch (Exception ignore) {
                // userSeq가 숫자가 아니면 null로 두거나 별도 처리
            }
        } 

        tbLogLoginRepository.save(log);
    }
    
}
