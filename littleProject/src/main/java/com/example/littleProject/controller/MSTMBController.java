package com.example.littleProject.controller;

import com.example.littleProject.controller.dto.response.StatusResponse;
import com.example.littleProject.service.MSTMBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1")
public class MSTMBController {
    @Autowired
    private MSTMBService mstmbService;

    @PutMapping("/mstmb/updateCurPrice")
    public String updateCurPriceInMSTMBByStock(@RequestParam BigDecimal curPrice, @RequestParam String stock){
        String response = this.mstmbService.updateCurPriceInMSTMBByStock(curPrice, stock);
        return response;
    }
}
