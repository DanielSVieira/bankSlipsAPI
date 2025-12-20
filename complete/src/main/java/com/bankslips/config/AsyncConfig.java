package com.bankslips.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {
	
	private static final int CORES_OVER_CAPACITY = 2;
	
    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * CORES_OVER_CAPACITY);
    }

}
