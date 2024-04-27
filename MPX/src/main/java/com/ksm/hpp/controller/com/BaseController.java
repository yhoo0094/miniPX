package com.ksm.hpp.controller.com;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.ksm.hpp.framework.interceptor.LoginCheckInterceptor;

@Controller
public class BaseController {
	protected static final Log log = LogFactory.getLog("Application");
	protected static final Logger logger = Logger.getLogger(BaseController.class);
}
