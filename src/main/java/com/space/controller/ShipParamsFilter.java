package com.space.controller;

import com.space.model.Ship;

import java.util.ArrayList;
import java.util.List;

public class ShipParamsFilter {
    public static int DEFAULT_PAGE_NUMBER = 1;
    public static int DEFAULT_PAGE_SIZE = 3;

    public static List<Ship> filterShipListByPages(List<Ship> ships, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? DEFAULT_PAGE_NUMBER : pageNumber + DEFAULT_PAGE_NUMBER;
        pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
        List<Ship> resultShips = new ArrayList<>();
        int index1 = pageNumber * pageSize - pageSize;
        int index2 = pageNumber * pageSize;
        for (int i = index1; i < index2; i++) {
            try {
                resultShips.add(ships.get(i));
            }
            catch(IndexOutOfBoundsException e) {
                break;
            }
        }
        return resultShips;
    }
}
