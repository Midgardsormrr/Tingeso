package com.example.demo.repositories;

import com.example.demo.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PricingRepository extends JpaRepository<PricingEntity, Long> {
    Optional<Object> findByLaps(int laps);

    @Query("SELECT SUM(p.totalDuration) FROM PricingEntity p WHERE p.laps = :laps")
    Integer getTotalDurationByLaps(@Param("laps") int laps);


}
