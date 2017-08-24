package com.reptile.springboot;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.reptile.service.MobileService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootApplicationTests {

//    protected final Logger logger = LoggerFactory.getLogger(this.getClass());  
	private Logger logger = Logger.getLogger(MobileService.class);
	@Test
	public void contextLoads() {
	    logger.trace("I am trace log.");  
        logger.debug("I am debug log.");  
        logger.warn("I am warn log.");  
        logger.error("I am error log.");  
        logger.info("I am info log.");  
	}

}


