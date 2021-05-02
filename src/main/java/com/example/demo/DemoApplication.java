package com.example.demo;

import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {

    @SneakyThrows
    public static void main(String[] args) {
        //SpringApplication.run(DemoApplication.class, args);

        CoffeeMakerApplication cma = new CoffeeMakerApplication();

        List<String> input = new ArrayList<>();
        input.add("hot_tea");
        input.add("hot_coffee");
        input.add("black_tea");
        input.add("green_tea");

        Map<String, Integer> itemQty = new HashMap<>();
        itemQty.put("hot_water", 1000);
        itemQty.put("green_mixture", 100);
        itemQty.put("sugar_syrup", 100);
        itemQty.put("hot_milk", 500);

        cma.configureMachine("https://api.npoint.io/e8cd5a9bbd1331de326a");
        cma.getIngredients();

        cma.runMachine(input);
        cma.getIngredients();

        cma.updateIngredients(itemQty);
        cma.getIngredients();

        cma.runMachine(input);
        cma.getIngredients();
    }
}
