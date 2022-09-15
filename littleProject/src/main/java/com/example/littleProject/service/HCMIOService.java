package com.example.littleProject.service;

import com.example.littleProject.model.HCMIORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HCMIOService {
    @Autowired
    private HCMIORepository hcmioRepository;

}
