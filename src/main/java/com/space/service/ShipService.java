package com.space.service;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ShipService {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public Ship findById(Long id) {
        return shipRepository.getOne(id);
    }

    public List<Ship> findAll() {
        return shipRepository.findAll();
    }

    public Ship saveShip(Ship ship) {
        return shipRepository.save(ship);
    }

    ////////////////////////Валидации!??
    public void deleteById(Long id) {
        shipRepository.deleteById(id);
    }
}
