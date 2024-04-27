package logger.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log4j_Test {

    private static final Logger logger = LogManager.getLogger("Application");
    
    public static void main(String[] args) {
    	System.out.println("Test");
        logger.error("error, Hello, World!");
        logger.warn("warn, Hello, World!");
        logger.info("info Hello, World!");
        logger.debug("debug Hello, World!");
        logger.trace("trace Hello, World!");
        logger.error("error, Hello, World!22");
    }
}

	