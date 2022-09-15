package com.example.littleProject.service;

import com.example.littleProject.controller.dto.response.ResultResponse;
import com.example.littleProject.controller.dto.response.StatusResponse;
import com.example.littleProject.model.entity.BSType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class Tool {
    //計算價金 amt
    public BigDecimal calcAmt(BigDecimal qty, BigDecimal price) {
        BigDecimal amt = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);
        return amt;
    }

    //計算 fee
    public BigDecimal calcFee(BigDecimal amt) {
        BigDecimal fee = amt.multiply(BigDecimal.valueOf(0.001425)).setScale(0, RoundingMode.HALF_UP);
        return fee;
    }

    //計算 tax
    public BigDecimal calcTax(BSType bstype, BigDecimal... amt) {
        //先判斷買賣
        if (BSType.B.equals(bstype)) {
            return BigDecimal.valueOf(0).setScale(0, RoundingMode.HALF_UP);
        } else {
            return amt[0].multiply(BigDecimal.valueOf(0.003)).setScale(0, RoundingMode.HALF_UP);
        }
    }

    //計算 NetAmt
    public BigDecimal calcNetAmt(BSType bstype, BigDecimal amt) {
        //先判斷買(-),賣(+)
        if (BSType.B.equals(bstype)) {
            return amt.add(calcFee(amt)).negate(); //購買NetAmt為負數
        } else {
            return amt.subtract(calcFee(amt)).subtract(calcTax(BSType.S, amt));
        }
    }

    //取得當前日期與時間 [0]yyyyMMdd, [1]HHmmss
    public String[] dateTimeNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        String[] dateTime = LocalDateTime.now().format(formatter).split(" ");
        return dateTime;
    }

    //取得calcMarKetValue
    public BigDecimal calcMarketValue(BigDecimal nowPrice, BigDecimal qty) {
        //nowprice*qty - fee - tax
        BigDecimal amt = calcAmt(nowPrice, qty);
        BigDecimal marKetValue = amt.subtract(calcFee(amt)).subtract(calcTax(BSType.S, amt));
        return marKetValue;
    }

    //取得UnrealProfit
    public BigDecimal calcUnrealProfit(BigDecimal marketValue, BigDecimal totalCost) {
        //(現值股票單價*交易股數-手續費-交易稅) - (買時股票單價*交易股數+手續費)
        // marKetValue - totalCost
        BigDecimal UnrealProfit = marketValue.subtract(totalCost);
        return UnrealProfit;
    }

    //取得profitability
    public String calcProfitMargin(BigDecimal unrealProfit, BigDecimal totalCost) {
        BigDecimal ProfitMargin = unrealProfit.divide(totalCost, 4, RoundingMode.HALF_UP); //4
        String profitMarginString = new DecimalFormat("#.00%").format(ProfitMargin);
        return profitMarginString;
    }

    //建立 StatusResponse
    public StatusResponse statusResponseBuilder(String responseCode, String message, List<? extends ResultResponse>... resultList) {
        StatusResponse.StatusResponseBuilder builder = new StatusResponse().builder();

        if ("002".equals(responseCode)) {
            return builder.message(message)
                    .responseCode(responseCode)
                    .resultList(new ArrayList<>())
                    .build();
        } else if ("001".equals(responseCode)) {
            return builder.message("查無符合資料")
                    .responseCode(responseCode)
                    .resultList(new ArrayList<>())
                    .build();
        } else if ("005".equals(responseCode)) {
            return builder.message("伺服器忙碌中，請稍後嘗試")
                    .responseCode(responseCode)
                    .resultList(new ArrayList<>())
                    .build();
        } else {
            return builder.message("")
                    .responseCode(responseCode)
                    .resultList(resultList[0])
                    .build();

        }
    }

}
