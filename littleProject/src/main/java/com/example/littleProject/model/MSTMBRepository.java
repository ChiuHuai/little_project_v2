package com.example.littleProject.model;

import com.example.littleProject.model.entity.MSTMB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MSTMBRepository extends JpaRepository<MSTMB, String> {
    MSTMB findByStock(String stock); //用於計算 未實現損益(取得現價)

    @Modifying
    @Transactional
    @Query(value = "UPDATE mstmb SET curPrice=?1, modDate=?2, modTime=?3,modUser=?4 WHERE stock =?5", nativeQuery = true)
    int updateCurPriceInMSTMBByStock(BigDecimal curPrice,String modDate,String modTime,String modUser, String stock);
    // 修改現價
}
