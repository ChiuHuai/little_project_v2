package com.example.littleProject.service;

import com.example.littleProject.model.entity.BSType;
import com.example.littleProject.model.entity.MSTMB;
import com.example.littleProject.model.entity.TCNUD;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CalcTool {
    //計算價金 amt
    public BigDecimal calcAmt(BigDecimal qty, BigDecimal price) {
        BigDecimal amt = qty.multiply(price).setScale(0, RoundingMode.HALF_UP); //整數 四捨五入
        return amt;
    }

    //計算 fee
    public BigDecimal calcFee(BigDecimal amt) {
        BigDecimal fee = amt.multiply(BigDecimal.valueOf(0.001425))
                .setScale(0, RoundingMode.HALF_UP);;
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

    //取得當前日期與時間
    public String[] dateTimeNow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
        String[] dateTime = LocalDateTime.now().format(formatter).split(" ");
        return dateTime;
    }

    //取得calcMarKetValue
    public BigDecimal calcMarketValue(BigDecimal nowPrice, BigDecimal qty){
        //nowprice*qty - fee - tax
        BigDecimal amt = calcAmt(nowPrice, qty);
        BigDecimal marKetValue = amt.subtract(calcFee(amt)).subtract(calcTax(BSType.S, amt));
        return marKetValue;
    }

    //取得UnrealProfit
    public BigDecimal calcUnrealProfit(BigDecimal marketValue, BigDecimal totalCost){
        //(現值股票單價*交易股數-手續費-交易稅) - (買時股票單價*交易股數+手續費)
        // marKetValue - totalCost
        BigDecimal UnrealProfit = marketValue.subtract(totalCost).setScale(0, RoundingMode.HALF_UP);
        return UnrealProfit;
    }

    public BigDecimal calcUnrealProfit1(MSTMB mstmb, TCNUD tcnud , BigDecimal totalCost) {
        BigDecimal amt = calcAmt(tcnud.getRemainQty(), mstmb.getCurPrice());
        BigDecimal currentValue = calcNetAmt(BSType.S, amt);
        BigDecimal UnrealProfit = currentValue.subtract(totalCost).setScale(0, RoundingMode.HALF_UP);
        return UnrealProfit;
    }
}
