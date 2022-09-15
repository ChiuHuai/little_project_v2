package com.example.littleProject.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SumResult extends ResultResponse{
    private String stock; //1
    private String stockName; //2
    private BigDecimal nowprice; //3
    private BigDecimal sumRemainQty; //4
    private BigDecimal sumFee; //5
    private BigDecimal sumCost; //6
    private BigDecimal sumMarketValue; //7
    private BigDecimal sumUnrealProfit; //8
    private String sumProfitMargin; //9
    private List<Result> detailList; //10


}
