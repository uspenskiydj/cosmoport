package com.space.controller;

import com.space.model.Ship;
import java.util.Comparator;
import java.util.List;

public class ShipSorter {

    public static void sortShipList(List<Ship> ships, ShipOrder order) {
        if (order == null) {
            order = ShipOrder.ID;
        }
        switch(order) {
            case DATE: ships.sort(Comparator.comparing(Ship::getProdDate)); break;
            case SPEED: ships.sort(Comparator.comparingDouble(Ship::getSpeed)); break;
            case RATING: ships.sort(Comparator.comparingDouble(Ship::getRating)); break;
            case ID: ships.sort(Comparator.comparingLong(Ship::getId)); break;
        }
    }
}
