package com.javier.parking.repository;

import com.javier.parking.model.ParkingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingHistoryRepository extends JpaRepository<ParkingHistory, Long> {
    List<ParkingHistory> findByPlate(String plate);
    List<ParkingHistory> findByExitTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}

