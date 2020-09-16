package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public ResponseEntity<List<Ship>> getShipsList(HttpServletRequest request) {
        List<Ship> ships = shipService.findAll();
        if (ships.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ships = ShipParamsFilter.getFilteredShipList(ships, request, true);

        return new ResponseEntity<>(ships, HttpStatus.OK);
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Integer> getShipsCount(HttpServletRequest request) {
        List<Ship> ships = shipService.findAll();

        if (ships == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ships = ShipParamsFilter.getFilteredShipList(ships, request, false);
        Integer shipCount = ships.size();

        return new ResponseEntity<>(shipCount, HttpStatus.OK);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (ShipValidator.haveNull(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!ShipValidator.isCorrectShip(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getIsUsed() == null) {
            ship.setIsUsed(false);
        }

        ship.setRating(countShipRating(ship));

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
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship, @PathVariable Long id) {
        if (id == null || id <= 0 || ship == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!ShipValidator.isCorrectShip(ship)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship updateShip = shipService.getById(id);
        if (updateShip == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String shipName = ship.getName();
        if (shipName != null) {
            updateShip.setName(shipName);
        }

        String shipPlanet = ship.getPlanet();
        if (shipPlanet != null) {
            updateShip.setPlanet(shipPlanet);
        }

        ShipType shipType = ship.getShipType();
        if (shipType != null) {
            updateShip.setShipType(shipType);
        }

        Double shipSpeed = ship.getSpeed();
        if (shipSpeed != null) {
            updateShip.setSpeed(shipSpeed);
        }

        Integer crewSize = ship.getCrewSize();
        if (crewSize != null) {
            updateShip.setCrewSize(crewSize);
        }

        Date shipProdDate = ship.getProdDate();
        if (shipProdDate != null) {
            updateShip.setProdDate(shipProdDate);
        }

        Boolean isUsed = ship.getIsUsed();
        if (isUsed != null) {
            updateShip.setIsUsed(isUsed);
        }

        updateShip.setRating(countShipRating(updateShip));

        shipService.saveShip(updateShip);

        return new ResponseEntity<>(updateShip, HttpStatus.OK);
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

    private double countShipRating(Ship ship) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        int shipProdYear = Integer.parseInt(sdf.format(ship.getProdDate()));
        double rating = 80 * ship.getSpeed() / (3020 - shipProdYear);
        if (ship.getIsUsed()) {
            rating /= 2;
        }
        rating = BigDecimal.valueOf(rating).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return rating;
    }
}
