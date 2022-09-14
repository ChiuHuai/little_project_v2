package com.example.littleProject.service;

import com.example.littleProject.model.HCMIORepository;
import com.example.littleProject.model.entity.HCMIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HCMIOService {
    @Autowired
    private HCMIORepository hcmioRepository;

    public List<HCMIO> getAllHCMIO() {
        List<HCMIO> hcmioList = this.hcmioRepository.findAll();
        return hcmioList;
    }

    public List<HCMIO> findByTradeDayAndBranchNoAndCustSeqAndDocseq(String tradeDate, String branchNo, String custSeq, String docSeq) {
        List<HCMIO> byTradeDayAndBranchNoAndCustSeqAndDocseq = this.hcmioRepository.findByTradeDateAndBranchNoAndCustSeqAndDocSeq(tradeDate, branchNo, custSeq, docSeq);
        return byTradeDayAndBranchNoAndCustSeqAndDocseq;
    }

}
