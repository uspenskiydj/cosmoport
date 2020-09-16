package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public ResponseEntity<List<Ship>> getShipsList(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "planet", required = false) String planet,
                                                   @RequestParam(value = "shipType", required = false) ShipType shipType,
                                                   @RequestParam(value = "after", required = false) Long after,
                                                   @RequestParam(value = "before", required = false) Long before,
                                                   @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                                   @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                                   @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                                   @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                                   @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                                   @RequestParam(value = "minRating", required = false) Double minRating,
                                                   @RequestParam(value = "maxRating", required = false) Double maxRating,
                                                   @RequestParam(value = "order", required = false) ShipOrder order,
                                                   @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        List<Ship> ships = shipService.findAll();
        if (ships.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Ship> filteredShips = new ArrayList<>();

        for (Ship ship: ships) {
            if (name != null && !ship.getName().contains(name)) {
                continue;
            }
            if (planet != null && !ship.getPlanet().contains(planet)) {
                continue;
            }
            if (shipType != null && ship.getShipType() != shipType) {
                continue;
            }
            if (after != null && after > ship.getProdDate().getTime()) {
                continue;
            }
            if (before != null && before < ship.getProdDate().getTime()) {
                continue;
            }
            if (isUsed != null && !ship.getIsUsed().equals(isUsed)) {
                continue;
            }
            if (minSpeed != null && ship.getSpeed() < minSpeed) {
                continue;
            }
            if (maxSpeed != null && ship.getSpeed() > maxSpeed) {
                continue;
            }
            if (minCrewSize != null && ship.getCrewSize() < minCrewSize) {
                continue;
            }
            if (maxCrewSize != null && ship.getCrewSize() > maxCrewSize) {
                continue;
            }
            if (minRating != null && ship.getRating() < minRating) {
                continue;
            }
            if (maxRating != null && ship.getRating() > maxRating) {
                continue;
            }
            filteredShips.add(ship);
        }

        pageNumber = pageNumber == null ? 1 : pageNumber + 1;
        pageSize = pageSize == null ? 3 : pageSize;

        List<Ship> resultShips = new ArrayList<>();
        int index1 = pageNumber*pageSize - pageSize;
        int index2 = pageNumber*pageSize;
        for (int i = index1; i < index2; i++) {
            try {
                resultShips.add(filteredShips.get(i));
            }
            catch(Exception e) {
                break;
            }
        }

        ShipSorter.sortShipList(resultShips, order);

        return new ResponseEntity<>(resultShips, HttpStatus.OK);
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
        if (shipSpeed < 0.01 && shipSpeed > 0.99) {
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
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship, @PathVariable Long id) {
        if (id == null || id <= 0 || ship == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship updateShip = shipService.getById(id);

        if (updateShip == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String shipName = ship.getName();
        if (shipName != null) {
            if (shipName.isEmpty() || shipName.length() > 50) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            updateShip.setName(shipName);
        }

        String shipPlanet = ship.getPlanet();
        if (shipPlanet != null) {
            if (shipPlanet.isEmpty() || shipPlanet.length() > 50) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            updateShip.setPlanet(shipPlanet);
        }

        ShipType shipType = ship.getShipType();
        if (shipType != null) {
            updateShip.setShipType(shipType);
        }

        Double shipSpeed = ship.getSpeed();
        if (shipSpeed != null) {
            shipSpeed = BigDecimal.valueOf(shipSpeed).setScale(2, RoundingMode.HALF_UP).doubleValue();
            if (shipSpeed < 0.01 || shipSpeed > 0.99) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            updateShip.setSpeed(shipSpeed);
        }

        Integer crewSize = ship.getCrewSize();
        if (crewSize != null) {
            if (crewSize < 1 || crewSize > 9999) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            updateShip.setCrewSize(crewSize);
        }

        try {
            Date shipProdDate = ship.getProdDate();
            if (shipProdDate != null) {
                long prodDate = shipProdDate.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                if (prodDate < sdf.parse("2800").getTime()
                        || prodDate > sdf.parse("3019").getTime()) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                updateShip.setProdDate(shipProdDate);
            }
        }
        catch(ParseException ignored) {
        }

        Boolean isUsed = ship.getIsUsed();
        if (isUsed != null) {
            updateShip.setIsUsed(isUsed);
        }

        updateShip.setRating(getShipRating(updateShip));

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
