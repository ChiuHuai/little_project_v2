package com.example.littleProject.controller.dto.request;

import com.example.littleProject.model.entity.BSType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private String tradeDate;
    private String branchNo;
    private String custSeq;
    private String docSeq;
    private String stock;
    private BigDecimal price;
    private BigDecimal qty;

}
