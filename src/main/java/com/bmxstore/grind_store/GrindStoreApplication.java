package com.bmxstore.grind_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@SpringBootConfiguration
public class GrindStoreApplication {

	//FIXed
	// TODO: 16.03.2022 fix all sonar issues include naming violations
	public static void main(String[] args) {
		SpringApplication.run(GrindStoreApplication.class, args);
	}

}
