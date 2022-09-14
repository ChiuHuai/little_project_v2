package com.example.littleProject.controller;

import com.example.littleProject.model.entity.HCMIO;
import com.example.littleProject.service.HCMIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class HCMIOController {
    @Autowired
    private HCMIOService hcmioService;

    @GetMapping("/hcmio")
    public List<HCMIO> getAllHCMIO() {
        List<HCMIO> hcmioList = this.hcmioService.getAllHCMIO();
        if (hcmioList.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found.");
        }
        return hcmioList;
    }

}
