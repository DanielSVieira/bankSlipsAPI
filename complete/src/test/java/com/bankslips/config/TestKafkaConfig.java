package com.bankslips.config;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;


@TestConfiguration class TestKafkaConfig {
    @Bean
    public static BeanPostProcessor disableKafkaListeners() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) {
                // This stops the Kafka Annotation processor from looking for listeners
                if (bean instanceof KafkaListenerAnnotationBeanPostProcessor) {
                    return null; 
                }
                return bean;
            }
        };
    }
}
