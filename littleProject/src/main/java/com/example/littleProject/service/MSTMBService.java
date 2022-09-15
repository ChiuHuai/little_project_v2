package com.example.littleProject.service;

import com.example.littleProject.controller.dto.request.MSTMBRequest;
import com.example.littleProject.controller.dto.response.MSTMBResponse;
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

    @Autowired
    private Tool tool;

    public List<MSTMB> getAllMSTMB() {
        List<MSTMB> mstmbList = this.mstmbRepository.findAll();
        return mstmbList;
    }

    @Cacheable(cacheNames = "stock", key = "#request.stock")
    public MSTMBResponse findByStock(MSTMBRequest request) {
        if (request.getStock().isBlank()) {
            return new MSTMBResponse().builder().message("參數為空，無法查詢").mstmb(null).build();
        }
        MSTMB mstmb = this.mstmbRepository.findByStock(request.getStock());
        if (null == mstmb) {
            return new MSTMBResponse().builder().message("查無結果").mstmb(null).build();
        }
        mstmb.setCurPrice(mstmb.getCurPrice().setScale(2, RoundingMode.HALF_UP));
        return new MSTMBResponse().builder().message("成功").mstmb(mstmb).build();
    }

    @CachePut(cacheNames = "stock", key = "#request.stock")
    public MSTMBResponse updateCurPriceInMSTMBByStock(MSTMBRequest request) {

        MSTMB mstmb = this.mstmbRepository.findByStock(request.getStock());
        System.out.println(mstmb.getModTime());
        if (null == mstmb) {
            return new MSTMBResponse().builder().message("查無結果").mstmb(null).build();
        }

        BigDecimal curPrice = request.getCurPrice().setScale(2, RoundingMode.HALF_UP);

        String[] dateTimeNow = this.tool.dateTimeNow();
        this.mstmbRepository.updateCurPriceInMSTMBByStock(curPrice,dateTimeNow[0],dateTimeNow[1],"HuaiChiu", request.getStock());
        MSTMB newMSTMB = this.mstmbRepository.findByStock(request.getStock());
        newMSTMB.setCurPrice(curPrice);
        newMSTMB.setModDate(dateTimeNow[0]);
        newMSTMB.setModTime(dateTimeNow[1]);
        System.out.println(newMSTMB.getModTime());

        return new MSTMBResponse().builder().message("成功").mstmb(newMSTMB).build();
    }
}
