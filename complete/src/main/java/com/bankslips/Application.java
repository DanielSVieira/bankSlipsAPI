package com.bankslips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(scanBasePackages = "com.bankslips")
@EnableJpaRepositories("com.bankslips.repository")
@EnableScheduling
@EntityScan(basePackages = {"com.banklips.domain", "com.exchangerate.domain"})  
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
