package com.space.controller;

import com.space.model.Ship;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//@RestController
@Controller
@RequestMapping("/rest")
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping("/ships")
    public String getShipsList(Model model) {
        List<Ship> ships = shipService.findAll();
        model.addAttribute("ships", ships);

        return "test";
//        return "index";
    }

    @GetMapping("/ships/count")
    public String getShipsCount() {


        return "test";
//        return "index";
    }

    @PostMapping("/ships")
    public String createShip(Ship ship) {
        shipService.saveShip(ship);

        return "test";
//        return "index";
    }

    @GetMapping("/ships/{id}")
    public String getShip(@PathVariable Long id) {
        shipService.findById(id);

        return "test";
//        return "index";
    }

    @PostMapping("/ships/{id}")
    public String updateShip(@PathVariable Long id) {


        return "test";
//        return "index";
    }

    @DeleteMapping("/ships/{id}")
    public String deleteShip(@PathVariable Long id) {
        shipService.deleteById(id);

        return "test";
//        return "index";
    }
}
