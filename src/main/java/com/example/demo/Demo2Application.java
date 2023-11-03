package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo2Application {
	static final Logger log =
			LoggerFactory.getLogger(Demo2Application.class);
	public static void main(String[] args) {

		SpringApplication.run(Demo2Application.class, args);

	}

}
