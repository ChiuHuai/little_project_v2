package com.example.littleProject.model;

import com.example.littleProject.model.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, String> {
    String findByHoliday(String holiday);
}
