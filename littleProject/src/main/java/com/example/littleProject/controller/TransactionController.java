package com.example.littleProject.controller;

import com.example.littleProject.controller.dto.request.TransactionRequest;
import com.example.littleProject.controller.dto.request.UnrealRequest;
import com.example.littleProject.controller.dto.response.Result;
import com.example.littleProject.controller.dto.response.StatusResponse;
import com.example.littleProject.controller.dto.response.SumResult;
import com.example.littleProject.model.entity.MSTMB;
import com.example.littleProject.model.entity.TCNUD;
import com.example.littleProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@RestController
//@EnableTransactionManagement
@RequestMapping("/api/v1")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private HCMIOService hcmioService;
    @Autowired
    private TCNUDService tcnudService;
    @Autowired
    private MSTMBService mstmbService;

    @Autowired
    private CalcTool calcTool;

    @PostMapping("/unreal/detail")
    public StatusResponse detailsOfUnrealizedProfit(@Valid @RequestBody UnrealRequest request) {
        List<Result> resultList = this.transactionService.detailsOfUnrealizedProfit(request);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setMessage("");
        statusResponse.setResponseCode("000");
        statusResponse.setResultList(resultList);
        return statusResponse;
    }

    @PostMapping("/unreal/sum")
    public StatusResponse sumOfUnrealizedProfit(@Valid @RequestBody UnrealRequest request) {
        List<SumResult> sumResultList = this.transactionService.sumOfUnrealizedProfit(request);
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setMessage("");
        statusResponse.setResponseCode("000");
        statusResponse.setResultList(sumResultList);
        return statusResponse;
    }

    @PostMapping("/unreal/add")
    public StatusResponse addUnreal(@RequestBody TransactionRequest request) {
        String responseMessage = null;
        try {
            responseMessage = this.transactionService.buyStock(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StatusResponse statusResponse = new StatusResponse();
        if (responseMessage.equals("")) {
            Result result = new Result();

            //找到相同書號，加入資料到statusResponse 的 ResultList
            TCNUD tcnud = this.tcnudService.findTCNUDByDocSeq(request.getDocSeq());
            result.setTradeDate(tcnud.getTradeDate());
            result.setDocSeq(tcnud.getDocSeq());
            result.setStock(tcnud.getStock());

            //不確定
            BigDecimal buyPrice = tcnud.getPrice().setScale(2, RoundingMode.HALF_UP); //在一開始時維持後兩位
            result.setBuyprice(buyPrice);
            result.setQty(tcnud.getQty());
            result.setRemainQty(tcnud.getRemainQty());
            result.setFee(tcnud.getFee());
            result.setCost(tcnud.getCost());

            MSTMB mstmb = this.mstmbService.findByStock(request.getStock());
            result.setStockName(mstmb.getStockName());

            //要防沒資料
            BigDecimal nowPrice = mstmb.getCurPrice().setScale(2, RoundingMode.HALF_UP);
            result.setNowprice(nowPrice); // from MSTMB

            BigDecimal marketValue = this.calcTool.calcMarketValue(nowPrice, tcnud.getQty());
            BigDecimal unrealProfit = this.calcTool.calcUnrealProfit(marketValue, tcnud.getCost());
            result.setMarketValue(marketValue);
            result.setUnrealProfit(unrealProfit);

            //注意四捨五入
            List<Result> resultList = new ArrayList<>();
            resultList.add(result);
            statusResponse.setResultList(
                    resultList
            );

            statusResponse.setResponseCode("000");
            statusResponse.setMessage(responseMessage);
        }


        return statusResponse;
    }

    //用於計算 未實現損益
    public BigDecimal calcUnrealProfit(String stock) {
        MSTMB mstmb = this.mstmbService.findByStock(stock); //股票資訊 now price
        TCNUD tcnud = this.tcnudService.findLatestStock(stock);
        BigDecimal sumOfNetAmt = this.hcmioService.SumOfNetAmt(stock);

        BigDecimal UnrealProfit = calcTool.calcUnrealProfit1(mstmb, tcnud, sumOfNetAmt);
        return UnrealProfit;
    }

}
