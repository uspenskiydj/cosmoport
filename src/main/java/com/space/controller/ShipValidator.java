package com.space.controller;

import com.space.model.Ship;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShipValidator {

    public static boolean haveNull(Ship ship) {
        if (ship == null || ship.getName() == null || ship.getPlanet() == null || ship.getShipType() == null
                || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null) {
            return true;
        }
        return false;
    }

    public static boolean isCorrectShip(Ship ship) {
        String name = ship.getName();
        if (name != null && (ship.getName().isEmpty() || ship.getName().length() > 50)) {
            return false;
        }

        String planet = ship.getPlanet();
        if (planet != null && (ship.getPlanet().isEmpty() || ship.getPlanet().length() > 50)) {
            return false;
        }

        Double shipSpeed = ship.getSpeed();
        if (shipSpeed != null) {
            shipSpeed = BigDecimal.valueOf(shipSpeed).setScale(2, RoundingMode.HALF_UP).doubleValue();
            if (shipSpeed < 0.01 && shipSpeed > 0.99) {
                return false;
            }
        }

        Integer crewSize = ship.getCrewSize();
        if (crewSize != null && (!(crewSize >= 1 && crewSize <= 9999))) {
            return false;
        }

        try {
            Date date = ship.getProdDate();
            if (date != null) {
                long prodDate = ship.getProdDate().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                if (prodDate < sdf.parse("2800").getTime()
                        || prodDate > sdf.parse("3019").getTime()) {
                    return false;
                }
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
