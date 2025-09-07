package com.si.mindhealth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MindhealthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MindhealthApplication.class, args);
	}

}
