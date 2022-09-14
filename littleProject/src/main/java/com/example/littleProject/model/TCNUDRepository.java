package com.example.littleProject.model;

import com.example.littleProject.model.entity.TCNUD;
import com.example.littleProject.model.entity.TCNUDId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TCNUDRepository extends JpaRepository<TCNUD, TCNUDId> {

    List<TCNUD> findByCustSeqAndBranchNo(String custSeq, String branchNo); //第一題 detail

    List<TCNUD> findByStockAndCustSeqAndBranchNo(String stock, String custSeq, String branchNo); //第一題 detail

    List<TCNUD> findByTradeDateAndBranchNoAndCustSeq(String tradeDate, String branchNo, String custSeq);

    TCNUD findByTradeDateAndBranchNoAndCustSeqAndDocSeq(String tradeDate, String branchNo, String custSeq, String docSeq);


    @Query(value = "SELECT * FROM tcnud WHERE stock=?1 AND branchNo=?2 AND custSeq=?3 ORDER BY modDate DESC , modTime Desc LIMIT 1",
            nativeQuery = true)
    TCNUD findLatestStock(String stock, String branchNo, String custSeq); //用於計算 未實現損益(取得最新一筆剩餘股數)

}
