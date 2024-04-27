package com.ksm.hpp.framework.filter;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;

public class HttpServletRequestWrapper extends ServletRequestWrapper {

	public HttpServletRequestWrapper(ServletRequest request) {
		super(request);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
			return super.getInputStream();
	}

	@Override
	public BufferedReader getReader() throws IOException {
			return super.getReader();
	}

}

	