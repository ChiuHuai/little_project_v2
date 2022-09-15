package com.example.littleProject.controller;

import com.example.littleProject.controller.dto.request.MSTMBRequest;
import com.example.littleProject.controller.dto.response.MSTMBResponse;
import com.example.littleProject.service.MSTMBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MSTMBController {
    @Autowired
    private MSTMBService mstmbService;

    @PostMapping("/mstmb/updateCurPrice")
    public MSTMBResponse updateCurPriceInMSTMBByStock(@RequestBody MSTMBRequest request){
        MSTMBResponse response = this.mstmbService.updateCurPriceInMSTMBByStock(request);
        return response;
    }

    @PostMapping("/mstmb/findByStock")
    public MSTMBResponse findByStock(@RequestBody MSTMBRequest request){
        MSTMBResponse response = this.mstmbService.findByStock(request);
        return response;
    }
}
