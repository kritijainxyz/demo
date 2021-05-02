package com.example.demo.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonDeserialize
public class CoffeeMachine {

    @JsonProperty("outlets")
    Map<String, Integer> outlets;

    @JsonProperty("beverages")
    Map<String, Map<String, Integer>> beverages;

    @JsonProperty("total_items_quantity")
    Map<String, Integer> itemQuantity;
}

