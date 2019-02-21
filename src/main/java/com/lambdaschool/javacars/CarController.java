package com.lambdaschool.javacars;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class CarController {
    private final CarRepository carrepos;
    private final RabbitTemplate rt;

    public CarController(CarRepository carrepos, RabbitTemplate rt) {
        this.carrepos = carrepos;
        this.rt = rt;
    }

    // ### GET

    // /cars/id/{id}
    // returns the car based of of id

    @GetMapping("/cars/id/{id}")
    public Car findCar(@PathVariable Long id) {
        return carrepos.findById(id)
                .orElseThrow(() ->
                        new CarNotFoundException("Car record with id " + id + " does not exist."));
    }

    // /cars/year/{year}
    // returns a list of cars of that year model

    @GetMapping("/cars/year/{year}")
    public List<Car> getCarsByYear(@PathVariable int year) {
        List<Car> cars = carrepos.findAll();
        List<Car> yearMatch = new ArrayList<>();

        for (Car c : cars) {
            if (c.getYear() == year) {
                yearMatch.add(c);
            }
        }

        if (yearMatch.size() == 0) {
            throw new CarNotFoundException("There are no car records that match the year " + year + ".");
        }

        return yearMatch;
    }

    // /cars/brand/{brand}
    // returns a list of cars of that brand
    // This gets logged with a message of "search for {brand}".
    // So put the brand of the car that was searched in the message itself.

    @GetMapping("/cars/brand/{brand}")
    public List<Car> getCarsByBrand(@PathVariable String brand) {
        List<Car> cars = carrepos.findAll();
        List<Car> brandMatch = new ArrayList<>();

        for (Car c : cars) {
            if (c.getBrand().toLowerCase().equals(brand.toLowerCase())) {
                brandMatch.add(c);
            }
        }

        CarLog message = new CarLog("Searched for " + brand + ".");
        rt.convertAndSend(JavaCarsApplication.QUEUE_CAR_LOG, message.toString());

        if (brandMatch.size() == 0) {
            throw new CarNotFoundException("There are no car records that match the brand " + brand + ".");
        }

        return brandMatch;
    }

    // ### POST

    // /cars/upload
    // loads multiple sets of data from the RequestBody
    // This gets logged with a message of "Data loaded"

    @PostMapping("/cars/upload")
    public List<Car> uploadCars(@RequestBody List<Car> cars) {
        CarLog message = new CarLog("Data loaded.");
        rt.convertAndSend(JavaCarsApplication.QUEUE_CAR_LOG, message.toString());

        return carrepos.saveAll(cars);
    }

    // ### DELETE

    // /cars/delete/{id}
    // deletes a car from the list based off of the id
    // This gets logged with a message of "{id} Data deleted".
    // So, put the id of the car that got deleted in the message itself.

    @DeleteMapping("/cars/delete/{id}")
    public String deleteCar(@PathVariable Long id) {
        this.findCar(id);
        carrepos.deleteById(id);
        return "Successfully deleted the car record with the id " + id + '.';
    }
}
