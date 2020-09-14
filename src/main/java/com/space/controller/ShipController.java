package com.space.controller;

import com.space.model.Ship;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getShipsList() {
        List<Ship> ships = shipService.findAll();

        if (ships.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ships, HttpStatus.OK);
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Integer> getShipsCount() {
        List<Ship> ships = shipService.findAll();

        if (ships == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Integer shipCount = ships.size();

        return new ResponseEntity<>(shipCount, HttpStatus.OK);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getName() == null || ship.getPlanet() == null || ship.getShipType() == null
                || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getName().isEmpty() || ship.getName().length() > 50) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        double shipSpeed = BigDecimal.valueOf(ship.getSpeed()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (!(shipSpeed >= 0.01 && shipSpeed <= 0.99)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        int crewSize = ship.getCrewSize();
        if (!(crewSize >= 1 && crewSize <= 9999)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            long prodDate = ship.getProdDate().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            if (prodDate < sdf.parse("2800").getTime()
                    || prodDate > sdf.parse("3019").getTime()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        catch(ParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getIsUsed() == null) {
            ship.setIsUsed(false);
        }

        ship.setRating(getShipRating(ship));

        shipService.saveShip(ship);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShip(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship ship = shipService.getById(id);

        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(@RequestBody @PathVariable Long id, UriComponentsBuilder builder) {
        HttpHeaders headers = new HttpHeaders();

        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship ship = shipService.getById(id);

        shipService.saveShip(ship);

        return new ResponseEntity<>(ship, headers, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> deleteShip(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship ship = shipService.getById(id);

        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        shipService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private double getShipRating(Ship ship) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        int shipProdYear = Integer.parseInt(sdf.format(ship.getProdDate()));
        double rating = 80 * ship.getSpeed() / (3019 - shipProdYear + 1);
        if (ship.getIsUsed()) {
            rating /= 2;
        }
        rating = BigDecimal.valueOf(rating).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return rating;
    }
}
