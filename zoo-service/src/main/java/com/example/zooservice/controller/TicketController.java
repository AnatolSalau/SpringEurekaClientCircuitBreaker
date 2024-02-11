package com.example.zooservice.controller;



import com.example.lib.dto.AnimalDto;
import com.example.lib.dto.TicketDto;
import com.example.zooservice.component.RandomAnimalClient;
import com.example.zooservice.component.TicketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TicketController {
      private final TicketClient ticketClient;

      @Autowired
      public TicketController(TicketClient ticketClient) {
            this.ticketClient = ticketClient;
      }

      @GetMapping("/tickets/any")
      ResponseEntity<TicketDto> getAnyAnimal(){

            TicketDto ticketDto = ticketClient.randomByDiscoveryClient();

            return ResponseEntity
                  .ok()
                  .body(ticketDto);
      }

}
