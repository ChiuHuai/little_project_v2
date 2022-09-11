package com.example.littleProject.service;

import com.example.littleProject.controller.dto.request.CreateHCMIORequest;
import com.example.littleProject.model.HCMIORepository;
import com.example.littleProject.model.TCNUDRepository;
import com.example.littleProject.model.entity.HCMIO;
import com.example.littleProject.model.entity.TCNUD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HCMIOService {
    @Autowired
    private HCMIORepository hcmioRepository;

    @Autowired
    private TCNUDRepository tcnudRepository;

    public List<HCMIO> getAllHCMIO() {
        List<HCMIO> hcmioList = this.hcmioRepository.findAll();
        return hcmioList;
    }

    public List<HCMIO> findHCMIOByStock(String stock){
        List<HCMIO> hcmioList = this.hcmioRepository.findByStock(stock);
        return hcmioList;
    }

    //用於計算 未實現損益
    public BigDecimal SumOfNetAmt(String stock){
        return this.hcmioRepository.SumOfNetAmt(stock);
    }

}
