package com.example.zooservice.component;

import com.example.lib.dto.AnimalDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
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
      private final CircuitBreakerFactory circuitBreakerFactory;


      @Autowired
      public RandomAnimalClient(RestTemplate balancedRestTemplate, DiscoveryClient discoveryClient,
                                RestTemplate restTemplate,
                                CircuitBreakerFactory circuitBreakerFactory) {
            this.balancedRestTemplate = balancedRestTemplate;
            this.discoveryClient = discoveryClient;
            this.restTemplate = restTemplate;
            this.circuitBreakerFactory = circuitBreakerFactory;
      }

      //Balanced RestTemplate by @LoadBalanced from implementation 'org.springframework.cloud:spring-cloud-commons:4.1.1'
      //spring.application.name=random-animal есть в настройках Random Animal
      public AnimalDto randomByBalancedRestTemplate() {
            log.info("Sending  request for animal ");
            ResponseEntity<AnimalDto> randomAnimal = circuitBreakerFactory.create("randomAnimal").run(
                  () -> restTemplate.getForEntity("http://random-animal/random", AnimalDto.class),
                  throwable -> fallbackRandom());

            AnimalDto animalDto = randomAnimal.getBody();
            log.info("AnimalDto from RandomAnimal: {} ", animalDto);
            return animalDto;
      }

      public AnimalDto randomByDiscoveryClient() {
            log.info("Sending  request for animal ");
            ServiceInstance serviceInstance = discoveryClient.getInstances("random-animal")
                  .stream()
                  .findAny()
                  .orElseThrow(() -> new IllegalStateException("Random-animal service unavailable"));

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(
                  serviceInstance.getUri().toString() + "/random"
            );
            String uri = uriComponentsBuilder.toUriString();

            ResponseEntity<AnimalDto> randomAnimal = circuitBreakerFactory.create("randomAnimal").run(
                  () -> restTemplate.getForEntity(uri, AnimalDto.class),
                  throwable -> fallbackRandom());

            AnimalDto animalDto = randomAnimal.getBody();

            return animalDto;
      }

      public ResponseEntity<AnimalDto> fallbackRandom() {
            log.info("Fallback random");
            return ResponseEntity.ok().body(new AnimalDto("no animal"));
      }
}
