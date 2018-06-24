package com.bankslips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication (scanBasePackages={
		"com.bankslips.controller", "com.bankslips.service", "com.bankslips.service.implementation", "com.bankslips.repository", "com.bankslips.exception"})
@EnableJpaRepositories("com.bankslips.repository")
@EntityScan(basePackages = "com.banklips.domain")  
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
