package com.javier.parking.service;

import com.javier.parking.model.ParkingHistory;
import com.javier.parking.repository.ParkingHistoryRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class HistoryService {
    @Autowired
    private ParkingHistoryRepository parkingHistoryRepository;

    public List<ParkingHistory> findAll() {
        return parkingHistoryRepository.findAll();
    }

    public List<ParkingHistory> findByPlate(String plate) {
        return parkingHistoryRepository.findByPlate(plate);
    }
}
