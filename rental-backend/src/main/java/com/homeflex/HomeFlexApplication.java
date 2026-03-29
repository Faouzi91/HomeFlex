package com.homeflex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HomeFlexApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomeFlexApplication.class, args);
	}

}
