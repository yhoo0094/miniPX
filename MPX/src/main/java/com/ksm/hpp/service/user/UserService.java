package com.ksm.hpp.service.user;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ksm.hpp.framework.exception.ConfigurationException;
import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.framework.util.DateUtil;
import com.ksm.hpp.framework.util.PapangUtil;
import com.ksm.hpp.framework.util.RequestUtil;
import com.ksm.hpp.framework.util.StringUtil;
import com.ksm.hpp.service.com.BaseService;

@Service("UserService")
public class UserService extends BaseService {
	
	@Autowired
	SqlSession sqlSession; //SqlSession 빈 DI	
	
	/**
	 * @메소드명: insertUser
	 * @작성자: 김상민
	 * @생성일: 2022. 11. 2. 오후 6:59:00
	 * @설명: 사용자 생성
	 */	
	public Map<String, Object> insertUser(StringBuilder logStr, Map<String, Object> inData) throws Exception
	{
		Map<String, Object> result = new HashMap<String, Object>();
		int cnt = 0;
		
		//비밀번호 해쉬 처리
		//1 = SXRLcrGA1f5nK8A5cvZICY86tW2d/Rekkm3lrWEgqJU=
		String userPw = StringUtil.getSHA256("HPP" + (String)inData.get("userPw") + "MELONA");
		inData.put("userPw", userPw);
		
		do {
			//사용자 정보 등록
			cnt = sqlSession.insert("mapper.user.UserMapper.insertUser", inData);
			if(cnt == 0) {
				result.put(Constant.RESULT, Constant.RESULT_FAILURE);
				result.put(Constant.OUT_RESULT_MSG, "사용자 생성에 실패했습니다.");
				break;
			}

			//기본 사용자 그룹 매핑
			cnt = sqlSession.insert("mapper.user.UserMapper.insertDefaultGroup", inData);
			if(cnt == 0) {
				result.put(Constant.RESULT, Constant.RESULT_FAILURE);
				result.put(Constant.OUT_RESULT_MSG, "사용자 생성에 실패했습니다.");
				break;
			}
			
			result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
			result.put(Constant.OUT_DATA, cnt);
		} while(false);
		return result;
	}	
	
	/**
	 * @메소드명: login
	 * @작성자: 김상민
	 * @생성일: 2022. 11. 8. 오전 8:59:13
	 * @설명: 로그인
	 */
	public Map<String, Object> login(StringBuilder logStr, HttpServletRequest request, Map<String, Object> inData) throws Exception
	{
		Map<String, Object> result = new HashMap<String, Object>();
		int cnt;
		String loginCode;	//로그인_코드(01:로그인, 02:로그아웃, 03:존재하지 않는 사용자, 04: 잘못된 비밀번호, 05: 비밀번호 오입력 횟수 초과)
		
		//사용자 정보 조회
		Map<String, Object> loginInfo = sqlSession.selectOne("mapper.user.UserMapper.selectUser", inData);
		
		do {
			if(loginInfo == null) {	//해당 계정이 조회되지 않을 때
				loginInfo = new HashMap();
				loginCode = "03";
				cnt = sqlSession.insert("mapper.user.UserMapper.insertLoginLog", inData);
				result.put(Constant.OUT_RESULT_MSG, "존재하지 않는 아이디이거나 비밀번호가 일치하지 않습니다.");	
				result.put(Constant.RESULT, Constant.RESULT_FAILURE);
				break;
			} else {
				//사용자 정책 조회
				inData.put("poliCode", "01");	//정책분류코드(01:사용자)
				List<Map<String, Object>> userPoli = sqlSession.selectList("mapper.com.CommonMapper.selectPoli", inData);
				result.put("userPoli", userPoli);
				
				//해당 계정의 비밀번호 오입력 횟수가 초과된 경우
				int pwErrCnt = Integer.parseInt((String) loginInfo.get("pwErrCnt"));	//사용자 비밀번호 오입력 횟수
				int pwErrCntLim = Integer.parseInt((String) PapangUtil.getMapFromList(userPoli, "poliNm", "PW_ERR_CNT_LIM").get("poliVal"));	//비밀번호 오입력 허용 횟수
				if(pwErrCnt >= pwErrCntLim) {	
					loginCode = "05";
					result.put(Constant.OUT_RESULT_MSG, "비밀번호 오입력 횟수가 초과되었습니다.");	
					result.put(Constant.RESULT, Constant.RESULT_FAILURE);
					break;
				}
				
				//비밀번호 해쉬 처리
				String userPw = StringUtil.getSHA256("HPP" + (String)inData.get("userPw") + "MELONA");
				inData.put("userPw", userPw);				
				
				//비밀번호가 일치하지 않을 때
				if(!userPw.equals(loginInfo.get("userPw"))) {		
					cnt = sqlSession.update("mapper.user.UserMapper.pwErr", inData);
					if(cnt != 1) {throw new ConfigurationException("비밀번호 불일치 횟수 증가 오류 발생");}
					
					loginCode = "04";
					result.put(Constant.OUT_RESULT_MSG, "존재하지 않는 아이디이거나 비밀번호가 일치하지 않습니다.");	
					result.put(Constant.RESULT, Constant.RESULT_FAILURE);
					log.info(inData.get("userId") + " 계정 비밀번호 오입력 횟수 " + cnt + "회 증가");
					break;					
				}
				
				//비밀번호 유효기간이 만료된 경우
				int pswdLimDays = Integer.parseInt((String) PapangUtil.getMapFromList(userPoli, "poliNm", "PSWD_LIM_DAYS").get("poliVal"));	//비밀번호 변경 주기(일)
				String pwChDtti = (String) loginInfo.get("pwChDtti");	//비밀번호 최종수정일시
				String pswdLimDate = DateUtil.addDate(pwChDtti, 0, 0, pswdLimDays);	//비밀번호 변경 기한
				
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		        Calendar cal = Calendar.getInstance();
		        String strToday = sdf.format(cal.getTime());				
				
				if(DateUtil.isBefore(pswdLimDate, strToday)) {	//비밀번호 변경 기한이 현재 날짜보다 이전이면
					result.put(Constant.RESULT_DETAIL, Constant.PSWD_LIM_ISSUE);
				}
				
				//비밀번호 오입력 횟수 초기화
				cnt = sqlSession.update("mapper.user.UserMapper.resetPwErrCnt", inData);
				if(cnt != 1) {throw new ConfigurationException("비밀번호 오입력 횟수 초기화 오류 발생");}
				
				//사용자 정보 세션에 저장
				HttpSession session = request.getSession();
				loginInfo.remove("userPw");	//비밀번호 정보는 제거
				session.setAttribute(Constant.LOGIN_INFO, loginInfo);
				
				//권한 정보 세션에 저장
				inData.put("loginInfo", loginInfo);
				List<Map<String, Object>> authList = sqlSession.selectList("mapper.user.UserMapper.selectAuthList", inData);
				session.setAttribute(Constant.AUTH_LIST, authList);
				
				int sessionTime = Integer.parseInt((String) PapangUtil.getMapFromList(userPoli, "poliNm", "SESSION_TIME").get("poliVal"));	//세션 유지시간(초단위)
				session.setMaxInactiveInterval(sessionTime);	
				session.setAttribute(Constant.SESSION_TIME, sessionTime);	//세션 유지시간 정보 세션에 추가
			
				loginCode = "01";
				result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
				result.put("loginInfo", loginInfo);
			}
		} while(false);
		//로그인 IP 파악
		String ip = RequestUtil.getIpAddr(request);
		inData.put("ip", ip);
		loginInfo.put("ip", ip);
		
		//로그인 내역 저장
		inData.put("loginCode", loginCode);
		cnt = sqlSession.insert("mapper.user.UserMapper.insertLoginLog", inData);
		if(cnt != 1) {throw new ConfigurationException("로그인 내역 저장 오류 발생");}
		
		return result;
	}	
	
	/**
	 * @메소드명: logout
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 9. 오후 1:24:49
	 * @설명: 로그아웃
	 */
	public Map<String, Object> logout(StringBuilder logStr, HttpServletRequest request, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		int cnt;
		
		//로그아웃 내역 저장
		Map<String, Object> loginInfo = RequestUtil.getLoginInfo(request);
		loginInfo.put("loginCode", "02");	//로그인_코드(01:로그인, 02:로그아웃, 03:존재하지 않는 사용자, 04: 잘못된 비밀번호, 05: 비밀번호 오입력 횟수 초과)
		cnt = sqlSession.insert("mapper.user.UserMapper.insertLoginLog", loginInfo);
		if(cnt != 1) {throw new ConfigurationException("로그아웃 내역 저장 오류 발생");}
		
		//세션 로그인 정보 파기
		HttpSession session = request.getSession();
		session.invalidate();
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;		
	}
	
	/**
	 * 
	 * @메소드명: chkUniqId
	 * @작성자: 김상민
	 * @생성일: 2023. 1. 5. 오전 9:37:43
	 * @설명: 아이디 중복 체크
	 */
	public Map<String, Object> chkUniqId(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		Map<String, Object> data = sqlSession.selectOne("mapper.user.UserMapper.selectUser", inData);
		result.put("data", data);
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}		
	
	/**
	* @메소드명: chngUserPw
	* @작성자: KimSangMin
	* @생성일: 2024. 1. 18. 오후 2:00:03
	* @설명: 비밀번호 변경
	 */	
	public Map<String, Object> chngUserPw(StringBuilder logStr, Map<String, Object> inData) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		int cnt = 0;
		String userPw = StringUtil.getSHA256("HPP" + (String)inData.get("userPw") + "MELONA");
		inData.put("userPw", userPw);
		String bfoUserPwModal = StringUtil.getSHA256("HPP" + (String)inData.get("bfoUserPwModal") + "MELONA");
		inData.put("bfoUserPwModal", bfoUserPwModal);
		
		//비밀번호 일치여부 확인
		Boolean isFreeAuth = sqlSession.selectOne("mapper.user.UserMapper.userPwChk", inData);
		if(!isFreeAuth) {
			throw new ConfigurationException("기존 비밀번호가 일치하지 않습니다.");
		} else {
			cnt = sqlSession.update("mapper.user.UserMapper.chngUserPw", inData);
			if(cnt != 1) {
				throw new ConfigurationException("비밀번호 변경 과정에서 오류가 발생하였습니다.");
			}
		}
		
		result.put(Constant.RESULT, Constant.RESULT_SUCCESS);
		return result;
	}		
}
