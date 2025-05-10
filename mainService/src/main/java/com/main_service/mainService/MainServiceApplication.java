package com.main_service.mainService;

import com.main_service.mainService.Configs.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainServiceApplication {
	private static final Logger logger = LoggerFactory.getLogger(MainServiceApplication.class);


	public static void main(String[] args) {
		logger.info("catalina base ###" + ResourceConfig.CATALINA_BASE);
		logger.info("Java Home: " + System.getProperty("java.home"));
		logger.info("Java Version: " + System.getProperty("java.version"));
		SpringApplication.run(MainServiceApplication.class, args);
	}

}
