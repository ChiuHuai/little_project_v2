package com.example.littleProject.controller;

import com.example.littleProject.model.entity.TCNUD;
import com.example.littleProject.service.TCNUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tcnud")
public class TCNUDController {

    @Autowired
    private TCNUDService tcnudService;

    @GetMapping
    public List<TCNUD> getAllTCNUD(){
        List<TCNUD> tcnudList = this.tcnudService.getAllTCNUD();
        return tcnudList;
    }





}

