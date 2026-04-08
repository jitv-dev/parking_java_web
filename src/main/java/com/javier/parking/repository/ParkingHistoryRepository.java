package com.javier.parking.repository;

import com.javier.parking.model.ParkingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingHistoryRepository extends JpaRepository<ParkingHistory, Long> {
}
