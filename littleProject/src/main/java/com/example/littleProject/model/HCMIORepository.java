package com.example.littleProject.model;

import com.example.littleProject.model.entity.HCMIO;
import com.example.littleProject.model.entity.HCMIOId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HCMIORepository extends JpaRepository<HCMIO, HCMIOId> {

    List<HCMIO> findByTradeDateAndBranchNoAndCustSeqAndDocSeq(String tradeDate, String branchNo, String custSeq, String docSeq);

}
