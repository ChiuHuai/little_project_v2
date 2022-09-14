package com.example.littleProject.model;

import com.example.littleProject.model.entity.MSTMB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
public interface MSTMBRepository extends JpaRepository<MSTMB, String> {
    MSTMB findByStock(String stock); //用於計算 未實現損益(取得現價)

    @Modifying
    @Transactional
    @Query(value = "UPDATE mstmb SET curPrice = ?1 WHERE stock = ?2", nativeQuery = true)
    int updateCurPriceInMSTMBByStock(BigDecimal curPrice, String stock);

}
