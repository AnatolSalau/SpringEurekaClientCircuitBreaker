package com.example.zooservice.component;

import com.example.lib.dto.AnimalDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Component
public class RandomAnimalClient {
      private final RestTemplate balancedRestTemplate;
      private final DiscoveryClient discoveryClient;
      private final RestTemplate restTemplate;

      @Autowired
      public RandomAnimalClient(RestTemplate balancedRestTemplate, DiscoveryClient discoveryClient, RestTemplate restTemplate) {
            this.balancedRestTemplate = balancedRestTemplate;
            this.discoveryClient = discoveryClient;
            this.restTemplate = restTemplate;
      }

      //Balanced RestTemplate by @LoadBalanced from implementation 'org.springframework.cloud:spring-cloud-commons:4.1.1'
      //spring.application.name=random-animal есть в настройках Random Animal
      public AnimalDto randomByBalancedRestTemplate() {
            log.info("Sending  request for animal ");
            AnimalDto animalDto = balancedRestTemplate.getForEntity("http://random-animal/random",
                  AnimalDto.class).getBody();
            log.info("AnimalDto from RandomAnimal: {} ", animalDto);
            return animalDto;
      }

      public AnimalDto randomByDiscoveryClient() {
            List<ServiceInstance> instances = discoveryClient.getInstances("random-animal");
            System.out.println(instances);
            ServiceInstance serviceInstance = discoveryClient.getInstances("random-animal")
                  .stream()
                  .findAny()
                  .orElseThrow(() -> new IllegalStateException("Random-animal service unavailable"));

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(
                  serviceInstance.getUri().toString() + "/random"
            );
            String uri = uriComponentsBuilder.toUriString();

            AnimalDto animalDto = restTemplate.getForEntity(uri, AnimalDto.class).getBody();

            return animalDto;
      }
}
