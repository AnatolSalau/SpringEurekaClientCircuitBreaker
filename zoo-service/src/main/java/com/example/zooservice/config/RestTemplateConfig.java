package com.example.zooservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

      //Balanced RestTemplate from implementation 'org.springframework.cloud:spring-cloud-commons:4.1.1'
      @Bean
      @LoadBalanced
      RestTemplate balancedRestTemplate() {
            return new RestTemplate();
      }

      @Bean
      RestTemplate restTemplate() {
            return new RestTemplate();
      }
}
