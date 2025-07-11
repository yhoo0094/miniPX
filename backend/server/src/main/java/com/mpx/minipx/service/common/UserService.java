package com.mpx.minipx.service.common;

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
import com.mpx.minipx.entity.TbUser;
import com.mpx.minipx.framework.util.Constant;
import com.mpx.minipx.framework.util.JwtUtil;
import com.mpx.minipx.repository.RedisPermissionRepository;
import com.mpx.minipx.repository.TbTokenRepository;
//import com.mpx.minipx.framework.aop.ControllerAdvice;
import com.mpx.minipx.repository.TbUserRepository;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	protected static final Log log = LogFactory.getLog(BaseController.class);
//	protected static final Logger logger = LogManager.getLogger(ControllerAdvice.class);
	
	private final AuthService authService;
	
    @Autowired
    private SqlSessionTemplate sqlSession;    
    
    @Autowired
    private TbUserRepository tbUserRepository;
    
    @Autowired
    private TbTokenRepository tbTokenRepository;        
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;   
    
    @Autowired
    private RedisPermissionRepository redisPermissionRepository;      
    
    @Value("${jwt.secret}")
    private String jwtSecret;  // JWT 비밀 키 (application.properties에서 가져오기)    

    /**
     * @메소드명: getUserList
     * @작성자: KimSangMin
     * @생성일: 2025. 6. 19.
     * @설명:
     */
    public List<TbUser> getUserList() {
    	List<TbUser> result = tbUserRepository.findAll(); 
    	
    	return result;
//    	return sqlSession.selectList("com.mpx.minipx.mapper.UserMapper.getUserList");
    }
    
    /**
     * @메소드명: logout
     * @작성자: KimSangMin
     * @생성일: 2025. 7. 3.
     * @설명: 로그아웃
     */
    @Transactional
    public Map<String, Object> logout(String userId) {
    	Map<String, Object> result = new HashMap<>();
    	tbTokenRepository.deleteByUserId(userId);
    	return result;
    }    
    
    /**
     * @메소드명: login
     * @작성자: KimSangMin
     * @생성일: 2025. 6. 19.
     * @설명: 로그인
     */
    public Map<String, Object> login(Map<String, Object> inData) {
    	Map<String, Object> result = new HashMap<>();
    	
        String userId = (String) inData.get("userId");
        String userPw = (String) inData.get("userPw");
        
        // 사용자 조회
        TbUser user = tbUserRepository.findByUserId(userId);   
        
        if (user == null) {
            result.put("success", false);
            result.put("message", "존재하지 않는 사용자입니다.");
            return result;
        }
        
        // BCrypt로 비밀번호 비교
        if (!passwordEncoder.matches(userPw, user.getUserPw())) {
            result.put("success", false);
            result.put("message", "비밀번호가 일치하지 않습니다.");  
            return result;
        }
        
        //사용자 정보 저장
        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("userNm", user.getUserNm());
        result.put("userInfo", userInfo);

        // accessToken 생성
        String accessToken = JwtUtil.generateToken(user, jwtSecret, Constant.ACCESS_TOKEN_VALIDITY);
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setMaxAge((int)Constant.ACCESS_TOKEN_VALIDITY);
        accessCookie.setPath("/");     
        result.put("accessToken", accessCookie);	//컨트롤러에서 response에 추가해야함
        
        // refreshToken 생성
        String refreshToken = JwtUtil.generateToken(user, jwtSecret, Constant.REFRESH_TOKEN_VALIDITY);
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);  // JavaScript에서 쿠키에 접근 불가
        refreshCookie.setSecure(true);    // HTTPS에서만 전송
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
    	
        result.put("success", true);
        result.put("message", "로그인 성공");
    	
    	return result;
    }    
    
    
}
