package com.bmxstore.grind_store;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@SpringBootConfiguration
public class GrindStoreApplication {

	@Autowired
	ObjectMapper objectMapper;

	public static void main(String[] args) {
		SpringApplication.run(GrindStoreApplication.class, args);
	}

	@PostConstruct
	private void init(){
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}
}
