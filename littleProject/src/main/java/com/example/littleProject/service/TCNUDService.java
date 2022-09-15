package com.example.littleProject.service;

import com.example.littleProject.model.TCNUDRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TCNUDService {
    @Autowired
    private TCNUDRepository tcnudRepository;

}
