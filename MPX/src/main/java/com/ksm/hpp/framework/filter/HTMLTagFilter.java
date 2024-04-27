package com.ksm.hpp.framework.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ksm.hpp.framework.util.Constant;
import com.ksm.hpp.framework.util.RequestUtil;

public class HTMLTagFilter implements Filter {

	private FilterConfig config;
	private ArrayList<String> urlList;
	
    //1. Filter 인스턴스 시작
    public void init(FilterConfig config) throws ServletException {
    	//필터링에서 제외할 url을 urlList에 넣기
    	String urls = config.getInitParameter("excludePatterns");
    	StringTokenizer token = new StringTokenizer(urls, ",");
    	urlList = new ArrayList<String>();
    	
    	while(token.hasMoreTokens()) {
    		urlList.add(token.nextToken());
    	}
        this.config = config;
    }	
	
	//2. 실제 Filter 작업을 실행하는 구간
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		String url = req.getServletPath();
		Map<String, Object> inData;
		boolean isExclude = false;
		try {
			inData = RequestUtil.getParameterMap(req);
			
			//필터링 제외 url
			if(urlList.contains(url)) {isExclude = true;};
			
			//엑셀 다운로드 요청인 경우 필터링 제외
			if (inData.containsKey(Constant.EXCEL_FILENM)) {isExclude = true;};			
		} catch (Exception e) {
			e.printStackTrace();
		}   
		
		if(isExclude) {
			//필터링 제외된 경우
			chain.doFilter(req, (HttpServletResponse)response);
		} else {	
			chain.doFilter(new HTMLTagFilterRequestWrapper(req), response);
		}
	};
	
    //3. Filter 인스턴스 종료
    public void destroy() {
    
	}	
	
}

	