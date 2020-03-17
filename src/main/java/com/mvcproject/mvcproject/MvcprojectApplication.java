package com.mvcproject.mvcproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MvcprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MvcprojectApplication.class, args);
	}

}
