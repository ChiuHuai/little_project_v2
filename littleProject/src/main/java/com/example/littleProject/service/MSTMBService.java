package com.example.littleProject.service;

import com.example.littleProject.model.MSTMBRepository;
import com.example.littleProject.model.entity.MSTMB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MSTMBService {
    @Autowired
    private MSTMBRepository mstmbRepository;

    public List<MSTMB> getAllMSTMB(){
        List<MSTMB> mstmbList = this.mstmbRepository.findAll();
        return mstmbList;
    }

    @Cacheable(cacheNames = "stock" , key = "#stock")
    public MSTMB findByStock(String stock){
        MSTMB mstmb = this.mstmbRepository.findByStock(stock);
        System.out.println("from DB");
        return mstmb;
    }

    @CachePut(cacheNames = "stock",key = "#stock")
    public MSTMB updateCurPriceInMSTMBByStock(BigDecimal curPrice, String stock){
//        String response;
        MSTMB mstmb = this.mstmbRepository.findByStock(stock);
//        if(mstmb == null){
//            response = "This stock is not exist.";
//            return response;
//        }
//
        curPrice = curPrice.setScale(2, RoundingMode.HALF_UP);
        int count = this.mstmbRepository.updateCurPriceInMSTMBByStock(curPrice, stock);
        mstmb.setCurPrice(curPrice);
//        return response;
        return mstmb;
    }
}
