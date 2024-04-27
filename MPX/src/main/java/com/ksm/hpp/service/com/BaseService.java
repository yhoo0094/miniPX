package com.ksm.hpp.service.com;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.ksm.hpp.controller.com.BaseController;

@Service
public class BaseService
{
	protected static final Log log = LogFactory.getLog("Application");
	protected static final Logger logger = Logger.getLogger(BaseService.class);
}