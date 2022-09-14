package com.example.littleProject.service;

import com.example.littleProject.model.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HolidayService {

    @Autowired
    HolidayRepository holidayRepository;

    String findByHoliday(String holiday) {
        String Holiday = holidayRepository.findByHoliday(holiday);
        return Holiday;
    }
}
