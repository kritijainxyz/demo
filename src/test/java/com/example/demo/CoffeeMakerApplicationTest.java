package com.example.demo;

import com.example.demo.Entities.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoffeeMakerApplicationTest {

    CoffeeMakerApplication cma;

    @BeforeEach
    void configureMachine() {
        cma = new CoffeeMakerApplication();
        cma.configureMachine("https://api.npoint.io/e8cd5a9bbd1331de326a");
    }


    @Test
    void runMachineFlow() {
        Map<String, Integer> ingredients = cma.getIngredients();

        assertTrue((ingredients.containsKey("hot_milk")));
        assertEquals(500, ingredients.get("hot_milk"));

        List<String> input = new ArrayList<>();
        input.add("hot_tea");
        input.add("hot_coffee");
        input.add("black_tea");
        input.add("green_tea");

        int count_prepared = 0;
        int count_insufficient = 0;
        boolean hot_tea_coffee = false;

        List<Result> results1Run = cma.runMachine(input);
        Map<String, Integer> ingredientsAfter1Run = cma.getIngredients();
        for (Result result : results1Run) {
            if (result.getStatus().equals("prepared")) {
                count_prepared++;
                //if coffee and hot_tea are both prepared then hot milk should be finished
                if (result.getBeverageName().equals("hot_coffee") || result.getBeverageName().equals("hot_tea")) {
                    if (hot_tea_coffee) {
                        assertEquals(0, ingredientsAfter1Run.get("hot_milk"));
                    } else hot_tea_coffee = true;
                }
            }
            //count of beverages not prepared due to hot water shortage
            else if (result.getUnavailableIngredient().equals("hot_water")) {
                count_insufficient++;
            }

            if (result.getBeverageName().equals("green_tea")) {
                assertEquals("ingredient unavailable", result.getStatus());
                assertEquals("green_mixture", result.getUnavailableIngredient());
            }
        }
        assertEquals(2, count_prepared);
        assertEquals(1, count_insufficient);


        Map<String, Integer> itemQty = new HashMap<>();
        itemQty.put("hot_water", 1000);
        itemQty.put("green_mixture", 100);
        itemQty.put("sugar_syrup", 200);
        itemQty.put("tea_leaves_syrup", 300);
        itemQty.put("ginger_syrup", 100);

        //update ingredients
        cma.updateIngredients(itemQty);
        cma.getIngredients();
        List<Result> results2Run = cma.runMachine(input);


        int count_black_and_green_tea = 0;
        for (Result result : results2Run) {
            //black and green tea are always prepared
            if (result.getStatus().equals("prepared") && (result.getBeverageName().equals("black_tea") || result.getBeverageName().equals("green_tea"))) {
                count_black_and_green_tea++;
            }

            //insufficient ingredient is hot milk
            if (!result.getStatus().equals("prepared")) {
                assertEquals("hot_milk", result.getUnavailableIngredient());
            }

        }
        assertEquals(2, count_black_and_green_tea);
        cma.getIngredients();
    }

    @Test
    void testEmptyConfig() {
        //initialize with empty ingredients json and check that no beverage is made
    }
}