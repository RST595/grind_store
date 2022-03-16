package com.bmxstore.grindStore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jta.atomikos.AtomikosDependsOnBeanFactoryPostProcessor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class GrindStoreApplication {


	// TODO: 16.03.2022 fix all sonar issues include naming violations

	public static void main(String[] args) {
		SpringApplication.run(GrindStoreApplication.class, args);
	}

}
