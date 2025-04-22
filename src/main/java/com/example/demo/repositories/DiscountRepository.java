package com.example.demo.repositories;

import com.example.demo.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {
    List<DiscountEntity> findByTypeAndMinPeopleLessThanEqualAndMaxPeopleGreaterThanEqual(String group, int groupSize, int groupSize1);

    List<DiscountEntity> findByTypeAndMinVisitsLessThanEqualAndMaxVisitsGreaterThanEqual(String frequent, int visits, int visits1);
}
