package com.example.littleProject.controller;

import com.example.littleProject.controller.dto.response.StatusResponse;
import com.example.littleProject.model.entity.MSTMB;
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
    public MSTMB updateCurPriceInMSTMBByStock(@RequestParam BigDecimal curPrice, @RequestParam String stock){
        MSTMB mstmb = this.mstmbService.updateCurPriceInMSTMBByStock(curPrice, stock);
        return mstmb;
    }

    @GetMapping("/mstmb/findByStock")
    public MSTMB findByStock(@RequestParam String stock){
        MSTMB mstmb = this.mstmbService.findByStock(stock);
        return mstmb;
    }
}
