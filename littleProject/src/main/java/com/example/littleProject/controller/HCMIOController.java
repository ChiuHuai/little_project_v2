package com.example.littleProject.controller;

import com.example.littleProject.controller.dto.request.CreateHCMIORequest;
import com.example.littleProject.controller.dto.response.StatusResponse;
import com.example.littleProject.model.entity.HCMIO;
import com.example.littleProject.service.HCMIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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

    @GetMapping("/hcmio/stock")
    public List<HCMIO> findHCMIOByStock(@RequestParam String stock){
        List<HCMIO> hcmioList = this.hcmioService.findHCMIOByStock(stock);
        return hcmioList;
    }

    @GetMapping("/hcmio/SumOfNetAmt") //測試用
    public BigDecimal SumOfNetAmt(@RequestParam String stock){
        return this.hcmioService.SumOfNetAmt(stock);
    }



}
