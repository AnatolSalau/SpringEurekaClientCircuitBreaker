package com.example.ticketservice.controllers;

import com.example.lib.dto.TicketDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j

@RestController
@RequestMapping("/")
public class TicketController {
      @GetMapping()
      public ResponseEntity<TicketDto> ticket(){
            Random rand = new Random();
            TicketDto ticketDto = new TicketDto("ticket #"+rand.nextInt(1000));
            log.info("TicketDto : {}", ticketDto);
            return ResponseEntity.ok(ticketDto);
      }
}
