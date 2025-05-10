package com.rate_service.rateService;

import com.rate_service.rateService.Configurations.ResourseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RateServiceApplication {

	private static final Logger logger = LoggerFactory.getLogger(RateServiceApplication.class);

	public static void main(String[] args) {
		logger.info("catalina base ###" + ResourseConfig.CATALINA_BASE);
		logger.info("Java Home: " + System.getProperty("java.home"));
		logger.info("Java Version: " + System.getProperty("java.version"));
		SpringApplication.run(RateServiceApplication.class, args);
	}

}
