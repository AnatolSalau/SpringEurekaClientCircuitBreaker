package com.example.randomanimalservice.component;

import com.example.lib.dto.AnimalDto;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class AnimalDtoComponent {
      private List<AnimalDto> list = Arrays.asList(
            new AnimalDto("cat"),
            new AnimalDto("dog"),
            new AnimalDto("fox")
      );

      public AnimalDto random() {
            Random rand = new Random();
            return  list.get(rand.nextInt(list.size()));
      }
}
