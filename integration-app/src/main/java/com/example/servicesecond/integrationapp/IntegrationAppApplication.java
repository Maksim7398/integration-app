package com.example.servicesecond.integrationapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class IntegrationAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationAppApplication.class, args);
	}

}
