package com.example.littleProject.model;

import com.example.littleProject.model.entity.HCMIO;
import com.example.littleProject.model.entity.TCNUD;
import com.example.littleProject.model.entity.TCNUDId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TCNUDRepository extends JpaRepository<TCNUD, TCNUDId> {
    TCNUD findByDocSeq(String docSeq);

    List<TCNUD> findByStock(String stock); //第一題 detail

    @Query(value = "SELECT * FROM tcnud WHERE stock=?1 ORDER BY modDate DESC , modTime Desc LIMIT 1",
            nativeQuery = true)
    TCNUD findLatestStock(String stock); //用於計算 未實現損益(取得最新一筆剩餘股數)

}
