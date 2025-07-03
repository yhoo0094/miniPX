package com.mpx.minipx.controller.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {
	protected static final Log log = LogFactory.getLog("Application");
	protected static final Logger logger = LogManager.getLogger(BaseController.class);
}
