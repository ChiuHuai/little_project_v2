package com.example.littleProject.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Result extends ResultResponse{
    private String tradeDate; //1
    private String docSeq; //2
    private String stock; //3
    private String stockName; //4
    private BigDecimal buyprice; //5
    private BigDecimal nowprice; //6
    private BigDecimal qty; //7
    private BigDecimal remainQty; //8
    private BigDecimal fee; //9
    private BigDecimal cost; //10
    private BigDecimal marketValue; //11
    private BigDecimal unrealProfit; //12
    private String profitMargin; //13
}
