package com.example.demo;

import com.example.demo.Entities.CoffeeMachine;
import com.example.demo.Entities.CoffeeMaker;
import com.example.demo.Entities.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CoffeeMakerApplication {

    CoffeeMaker coffeeMaker;
    ObjectMapper objectMapper = new ObjectMapper();

    public List<Result> runMachine(List<String> input) {

        List<Result> resultList = new ArrayList<>();

        int outlets = coffeeMaker.getMachine().getOutlets().get("count_n");
        ExecutorService executor = Executors.newFixedThreadPool(outlets);

        List<Callable<Result>> callableTasks = input.stream().<Callable<Result>>map(s -> () -> {
            TimeUnit.MILLISECONDS.sleep(100);
            return makeBeverage(s);
        }).collect(Collectors.toList());

        //submit callable tasks list to executor service
        List<Future<Result>> futures = null;
        try {
            futures = executor.invokeAll(callableTasks);

            for (Future<Result> future : futures) {
                resultList.add(future.get());
            }
            //The executor shuts down softly by waiting a certain amount of time for termination of currently running tasks
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
        return resultList;
    }


    //when one thread is checking and updating ingredients quantity, others can't
    public synchronized Result makeBeverage(String beverageName) {
        //System.out.println(coffeeMaker.toString());
        Result result = new Result();

        CoffeeMachine machine = coffeeMaker.getMachine();
        Map<String, Integer> totalItemQuantityMap = machine.getItemQuantity();
        Map<String, Integer> ingredients = machine.getBeverages().get(beverageName);

        boolean ingredientsAvailable = true;
        String unavailableIngredient = "";
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            String beverageIngredient = entry.getKey();
            Integer beverageIngredientQty = entry.getValue();

            //if ingredient is unavailable
            if (!totalItemQuantityMap.containsKey(beverageIngredient)) {
                System.out.println(beverageName + " cannot be prepared because " + beverageIngredient + " is not available");
                result.setBeverageName(beverageName);
                result.setStatus("ingredient unavailable");
                result.setUnavailableIngredient(beverageIngredient);
                return result;
            }
            //store the first insufficient ingredient
            else if (ingredientsAvailable && beverageIngredientQty > totalItemQuantityMap.get(beverageIngredient)) {
                ingredientsAvailable = false;
                unavailableIngredient = beverageIngredient;
            }
        }

        //update ingredients if all required ingredients are sufficient and available
        if (ingredientsAvailable) {
            for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
                String beverageIngredient = entry.getKey();
                Integer beverageIngredientQty = entry.getValue();

                if (totalItemQuantityMap.containsKey(beverageIngredient)) {
                    int remainingQty = totalItemQuantityMap.get(beverageIngredient) - beverageIngredientQty;
                    totalItemQuantityMap.put(beverageIngredient, remainingQty);
                }
            }
            machine.setItemQuantity(totalItemQuantityMap);
            coffeeMaker.setMachine(machine);
            System.out.println(beverageName + " is prepared");
            result.setBeverageName(beverageName);
            result.setStatus("prepared");
            result.setUnavailableIngredient("");
            return result;
        } else {
            System.out.println(beverageName + " cannot be prepared because item " + unavailableIngredient + " is not sufficient");
            result.setBeverageName(beverageName);
            result.setStatus("ingredient insufficient");
            result.setUnavailableIngredient(unavailableIngredient);
            return result;
        }
    }

    //configure and initialize the machine with input json configs
    @SneakyThrows
    public void configureMachine(String inputUrl) {
        URL url = new URL(inputUrl);
        coffeeMaker = objectMapper.readValue(url, CoffeeMaker.class);
    }

    //update total available ingredients
    //Assumption: no outlets are serving during ingredients refill
    public void updateIngredients(Map<String, Integer> ingredients) {
        CoffeeMachine machine = coffeeMaker.getMachine();
        Map<String, Integer> totalItemQuantityMap = machine.getItemQuantity();
        for (Map.Entry<String, Integer> entry : ingredients.entrySet()) {
            String beverageIngredient = entry.getKey();
            Integer beverageIngredientQty = entry.getValue();

            //add new ingredient if not in list else update existing ingredient quantity
            int newQty = 0;
            if (totalItemQuantityMap.containsKey(beverageIngredient))
                newQty = totalItemQuantityMap.get(beverageIngredient);

            newQty += beverageIngredientQty;
            totalItemQuantityMap.put(beverageIngredient, newQty);

        }
        machine.setItemQuantity(totalItemQuantityMap);
        coffeeMaker.setMachine(machine);
    }

    //get total available ingredients
    public Map<String, Integer> getIngredients() {
        Map<String, Integer> itemQuantity = coffeeMaker.getMachine().getItemQuantity();
        System.out.println(itemQuantity.toString());
        return itemQuantity;
    }
}
