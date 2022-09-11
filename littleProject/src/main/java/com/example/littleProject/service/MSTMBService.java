package com.example.littleProject.service;

import com.example.littleProject.model.MSTMBRepository;
import com.example.littleProject.model.entity.MSTMB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MSTMBService {
    @Autowired
    private MSTMBRepository mstmbRepository;

    public List<MSTMB> getAllMSTMB(){
        List<MSTMB> mstmbList = this.mstmbRepository.findAll();
        return mstmbList;
    }

    public MSTMB findByStock(String stock){
        MSTMB mstmb = this.mstmbRepository.findByStock(stock);
        return mstmb;
    }
}
