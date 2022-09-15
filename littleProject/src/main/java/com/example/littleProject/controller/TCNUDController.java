package com.example.littleProject.controller;

import com.example.littleProject.service.TCNUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tcnud")
public class TCNUDController {

    @Autowired
    private TCNUDService tcnudService;

}

