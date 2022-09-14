package com.example.littleProject.service;

import com.example.littleProject.model.TCNUDRepository;
import com.example.littleProject.model.entity.TCNUD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TCNUDService {
    @Autowired
    private TCNUDRepository tcnudRepository;

    public List<TCNUD> getAllTCNUD() {
        List<TCNUD> tcnudList = this.tcnudRepository.findAll();
        return tcnudList;
    }
}
