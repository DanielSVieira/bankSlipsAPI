package com.bankslips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.bankslips.config.KafkaTopicProperties;


@SpringBootApplication(scanBasePackages = "com.bankslips")
@EnableJpaRepositories("com.bankslips.repository")
@EnableScheduling
@EnableAsync
@EntityScan(basePackages = {"com.bankslips.domain"}) 
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
