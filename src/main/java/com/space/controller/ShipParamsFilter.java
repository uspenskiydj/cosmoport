package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ShipParamsFilter {
    public static int DEFAULT_PAGE_NUMBER = 1;
    public static int DEFAULT_PAGE_SIZE = 3;

    public static List<Ship> getFilteredShipList(List<Ship> ships, HttpServletRequest request) {
        List<Ship> filteredShips = filterShipListByParams(ships, request);

        ShipSorter.sortShipList(filteredShips, request.getParameter("order"));

        filteredShips = ShipParamsFilter.filterShipListByPages(filteredShips,
                request.getParameter("pageNumber"),
                request.getParameter("pageSize"));

        return filteredShips;
    }

    private static List<Ship> filterShipListByParams(List<Ship> ships, HttpServletRequest request) {
        List<Ship> filteredShips = new ArrayList<>();
        for (Ship ship : ships) {
            String name = request.getParameter("name");
            if (name != null && !ship.getName().contains(name)) {
                continue;
            }
            String planet = request.getParameter("planet");
            if (planet != null && !ship.getPlanet().contains(planet)) {
                continue;
            }
            String shipType = request.getParameter("shipType");
            if (shipType != null && ship.getShipType() != ShipType.valueOf(shipType)) {
                continue;
            }
            String after = request.getParameter("after");
            if (after != null && Long.parseLong(after) > ship.getProdDate().getTime()) {
                continue;
            }
            String before = request.getParameter("before");
            if (before != null && Long.parseLong(before) < ship.getProdDate().getTime()) {
                continue;
            }
            String isUsed = request.getParameter("isUsed");
            if (isUsed != null && !ship.getIsUsed().equals(Boolean.valueOf(isUsed))) {
                continue;
            }
            String minSpeed = request.getParameter("minSpeed");
            if (minSpeed != null && ship.getSpeed() < Double.parseDouble(minSpeed)) {
                continue;
            }
            String maxSpeed = request.getParameter("maxSpeed");
            if (maxSpeed != null && ship.getSpeed() > Double.parseDouble(maxSpeed)) {
                continue;
            }
            String minCrewSize = request.getParameter("minCrewSize");
            if (minCrewSize != null && ship.getCrewSize() < Integer.parseInt(minCrewSize)) {
                continue;
            }
            String maxCrewSize = request.getParameter("maxCrewSize");
            if (maxCrewSize != null && ship.getCrewSize() > Integer.parseInt(maxCrewSize)) {
                continue;
            }
            String minRating = request.getParameter("minRating");
            if (minRating != null && ship.getRating() < Double.parseDouble(minRating)) {
                continue;
            }
            String maxRating = request.getParameter("maxRating");
            if (maxRating != null && ship.getRating() > Double.parseDouble(maxRating)) {
                continue;
            }
            filteredShips.add(ship);
        }
        return filteredShips;
    }

    private static List<Ship> filterShipListByPages(List<Ship> ships, String pageNumberStr, String pageSizeStr) {
        Integer pageNumber = pageNumberStr ==
                null ? DEFAULT_PAGE_NUMBER : Integer.parseInt(pageNumberStr) + DEFAULT_PAGE_NUMBER;
        Integer pageSize = pageSizeStr ==
                null ? DEFAULT_PAGE_SIZE : Integer.parseInt(pageSizeStr);
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
