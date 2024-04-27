package com.ksm.hpp.controller.com;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleController {
	protected static final Log log = LogFactory.getLog("Application");
	protected static final Logger logger = Logger.getLogger(ScheduleController.class);
	
	@Scheduled(initialDelay = 10000, fixedDelay = 1000 * 60 * 60 * 24)	//서버 시작 10초 후 작업 실행, 이후 24시간 후 작업 실행(테스트용)
	//@Scheduled(cron = "0 * * * * ?")	//매분 정각에 작업 실행(테스트용)
	//@Scheduled(fixedDelay = 5000)		//5초 마다 실행(테스트용)
	public void scheduleSample() {
		logger.info("스케줄러 실행 확인");
	}
}
