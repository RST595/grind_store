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

	public static void main(String[] args) {
		SpringApplication.run(GrindStoreApplication.class, args);
	}

}
