package com.example.zooservice.component;

import com.example.lib.dto.AnimalDto;
import com.example.lib.dto.TicketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class TicketClient {
      private final RestTemplate balancedRestTemplate;
      private final DiscoveryClient discoveryClient;
      private final RestTemplate restTemplate;

      private final CircuitBreakerFactory circuitBreakerFactory;

      @Autowired
      public TicketClient(RestTemplate balancedRestTemplate, DiscoveryClient discoveryClient, RestTemplate restTemplate,
                          CircuitBreakerFactory circuitBreakerFactory) {
            this.balancedRestTemplate = balancedRestTemplate;
            this.discoveryClient = discoveryClient;
            this.restTemplate = restTemplate;
            this.circuitBreakerFactory = circuitBreakerFactory;
      }

      //Balanced RestTemplate by @LoadBalanced from implementation 'org.springframework.cloud:spring-cloud-commons:4.1.1'
      //spring.application.name=random-animal есть в настройках Random Animal
      public TicketDto randomByBalancedRestTemplate() {
            log.info("Sending  request for ticket ");
            ResponseEntity<TicketDto> randomAnimal = circuitBreakerFactory.create("randomTicket").run(
                  () -> restTemplate.getForEntity("http://random-ticket/", TicketDto.class),
                  throwable -> fallbackRandom());

            TicketDto ticketDto = randomAnimal.getBody();
            log.info("TicketDto from Ticket service: {} ", ticketDto);
            return ticketDto;
      }

      public TicketDto randomByDiscoveryClient() {
            log.info("Sending  request for ticket ");
            ServiceInstance serviceInstance = discoveryClient.getInstances("random-ticket")
                  .stream()
                  .findAny()
                  .orElseThrow(() -> new IllegalStateException("Random-ticket service unavailable"));

            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(
                  serviceInstance.getUri().toString() + "/"
            );
            String uri = uriComponentsBuilder.toUriString();

            ResponseEntity<TicketDto> randomAnimal = circuitBreakerFactory.create("randomTicket").run(
                  () -> restTemplate.getForEntity(uri, TicketDto.class),
                  throwable -> fallbackRandom());

            TicketDto ticketDto = randomAnimal.getBody();
            log.info("TicketDto from Ticket service: {} ", ticketDto);

            return ticketDto;
      }
      public ResponseEntity<TicketDto> fallbackRandom() {
            log.info("Fallback random");
            return ResponseEntity.ok().body(new TicketDto("no ticket"));
      }
}
