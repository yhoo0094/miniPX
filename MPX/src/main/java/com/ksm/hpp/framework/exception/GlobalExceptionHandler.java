package com.ksm.hpp.framework.exception;

import javax.servlet.ServletException;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handle404(NoHandlerFoundException e) {
        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("errorMessage", "유효하지 않은 페이지입니다.");
        mav.addObject("errClass", e.getClass().toString());
        return mav;
    }
    
    //모든 종류의 예외를 처리
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception e) {
        return exceptionAction(e);
    }
    
    //NullPointerException이나 IllegalArgumentException과 같은 예외
    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class})
    public ModelAndView handleNullPointerException(Exception e) {
    	return exceptionAction(e);
    }
    
    //사용자 정의 예외
    @ExceptionHandler(ConfigurationException.class)
    public ModelAndView handleMyCustomException(ConfigurationException e) {
    	return exceptionAction(e);
    }
    
    //특정 HTTP 상태 코드와 관련된 예외
    @ExceptionHandler(HttpClientErrorException.class)
    public ModelAndView handleHttpClientErrorException(HttpClientErrorException e) {
    	return exceptionAction(e);
    }
    
    //데이터베이스 또는 다른 데이터 소스와의 상호 작용 중 발생하는 예외
    @ExceptionHandler(DataAccessException.class)
    public ModelAndView handleDataAccessException(DataAccessException e) {
    	return exceptionAction(e);
    }
    
    //서블릿 오류(jsp 파일이 없을 때 발생)
    @ExceptionHandler(ServletException.class)
    public ModelAndView handleServletException(ServletException e) {
    	return exceptionAction(e);
    }
    
    /**
    * @메소드명: exceptionAction
    * @작성자: KimSangMin
    * @생성일: 2023. 11. 29. 오후 8:02:05
    * @설명: 예외에 대한 공통 처리 메서드
     */
    public ModelAndView exceptionAction(Exception e) {
    	ModelAndView mav = new ModelAndView("error/error");
    	mav.addObject("errorMessage", "예상치 못한 오류 발생, 개발자에게 문의해주세요!");
    	mav.addObject("errClass", e.getClass().toString());
    	//mav.addObject("errorMessage", e.getMessage());	//DB에 메세지 넣기
    	return mav;
	}
}


	