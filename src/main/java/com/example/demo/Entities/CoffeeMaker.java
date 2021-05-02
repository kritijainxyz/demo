package com.example.demo.Entities;

import com.example.demo.Entities.CoffeeMachine;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonDeserialize
public class CoffeeMaker {
    @JsonProperty("machine")
    private CoffeeMachine machine;
}

