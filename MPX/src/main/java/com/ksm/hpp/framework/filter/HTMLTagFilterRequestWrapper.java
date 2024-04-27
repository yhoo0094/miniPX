package com.ksm.hpp.framework.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class HTMLTagFilterRequestWrapper extends HttpServletRequestWrapper {

	//생성자
	public HTMLTagFilterRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		if(value!=null){
			value = cleanXXS(value);
		}
		return value;
	}

	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		if(value!=null){
			value = cleanXXS(value);
		}
		return value;
	}	
	
	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if(values!=null){
			for (int i = 0, cnt = values.length; i < cnt; i++) {
				if (values[i] != null) {
					values[i] = cleanXXS(values[i]);
				}
			}			
		}
		return values;
	}
	
	/**
	* @메소드명: cleanXXS
	* @작성자: KimSangMin
	* @생성일: 2023. 1. 31. 오후 1:12:35
	* @설명: XXS 위험이 있는 문자 대체
	 */
	private String cleanXXS(String value) {
		value = value.replaceAll("&", "&amp;");
		value = value.replaceAll("/", "&#x2F;");
		value = value.replaceAll("\"", "&quot;");
		//value = value.replaceAll("'", "&apos;");	//IE에서 문제 발생할 수 있음
		value = value.replaceAll("'", "&#x27;");
		value = value.replaceAll("<", "&lt;");
		value = value.replaceAll(">", "&gt;");
		//value = value.replaceAll("(", "&#40;");
		//value = value.replaceAll(")", "&#41;");
		value = value.replaceAll(" ", "&nbsp;");
		value = value.replaceAll("\r", "<br>");
		value = value.replaceAll("\n", "<p>");
		return value;
	}
}

	