package com.example.littleProject.service;

import com.example.littleProject.controller.dto.request.TransactionRequest;
import com.example.littleProject.controller.dto.request.UnrealRequest;
import com.example.littleProject.controller.dto.response.Result;
import com.example.littleProject.controller.dto.response.SumResult;
import com.example.littleProject.model.HCMIORepository;
import com.example.littleProject.model.MSTMBRepository;
import com.example.littleProject.model.TCNUDRepository;
import com.example.littleProject.model.entity.BSType;
import com.example.littleProject.model.entity.HCMIO;
import com.example.littleProject.model.entity.MSTMB;
import com.example.littleProject.model.entity.TCNUD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private HCMIORepository hcmioRepository;

    @Autowired
    private TCNUDRepository tcnudRepository;

    @Autowired
    private MSTMBRepository mstmbRepository;

    @Autowired
    private CalcTool calcTool;

    public List<Result> detailsOfUnrealizedProfit(UnrealRequest request) {

        MSTMB mstmb = this.mstmbRepository.findByStock(request.getStock());
        BigDecimal nowPrice = mstmb.getCurPrice(); //取得現價
        String stockName = mstmb.getStockName(); //取得 stockName

        List<TCNUD> TCNUDList = this.tcnudRepository.findByStock(request.getStock());

        List<Result> resultList = new ArrayList<>();
        Result result;
        for (TCNUD tcnud : TCNUDList) {
            result = new Result();
            result.setTradeDate(tcnud.getTradeDate());
            result.setDocSeq(tcnud.getDocSeq());
            result.setStock(tcnud.getStock());
            result.setStockName(stockName);
            result.setBuyprice(tcnud.getPrice().setScale(2, RoundingMode.HALF_UP)); //
            result.setNowprice(nowPrice.setScale(2, RoundingMode.HALF_UP)); //
            result.setQty(tcnud.getQty());
            result.setRemainQty(tcnud.getRemainQty());
            result.setFee(tcnud.getFee());
            result.setCost(tcnud.getCost().setScale(0, RoundingMode.HALF_UP));//

            BigDecimal marketValue = this.calcTool.calcMarketValue(nowPrice, tcnud.getQty());
            result.setMarketValue(marketValue);
            result.setUnrealProfit(this.calcTool.calcUnrealProfit(marketValue, tcnud.getCost()));
            resultList.add(result);
        }
        return resultList;
    }

    public List<SumResult> sumOfUnrealizedProfit(UnrealRequest request){
        MSTMB mstmb = this.mstmbRepository.findByStock(request.getStock());
        BigDecimal nowPrice = mstmb.getCurPrice(); //取得現價
        String stockName = mstmb.getStockName(); //取得 stockName

        List<TCNUD> TCNUDList = this.tcnudRepository.findByStock(request.getStock());
        List<Result> resultList = new ArrayList<>();
        Result result;
        BigDecimal remainQty =  new BigDecimal(0);
        BigDecimal sumFee = new BigDecimal(0);
        BigDecimal sumCost = new BigDecimal(0);
        BigDecimal sumMarketValue = new BigDecimal(0);
        BigDecimal sumUnrealProfit = new BigDecimal(0);

        for (TCNUD tcnud : TCNUDList) {
            result = new Result();
            result.setTradeDate(tcnud.getTradeDate());
            result.setDocSeq(tcnud.getDocSeq());
            result.setStock(tcnud.getStock());
            result.setStockName(stockName);
            result.setBuyprice(tcnud.getPrice().setScale(2, RoundingMode.HALF_UP)); //
            result.setNowprice(nowPrice.setScale(2, RoundingMode.HALF_UP)); //
            result.setQty(tcnud.getQty());
            remainQty = tcnud.getRemainQty();
            result.setRemainQty(remainQty);

            BigDecimal fee = tcnud.getFee();
            result.setFee(fee);
            sumFee.add(fee);

            BigDecimal cost = tcnud.getCost().setScale(0, RoundingMode.HALF_UP);
            result.setCost(cost);
            sumCost.add(cost);

            BigDecimal marketValue = this.calcTool.calcMarketValue(nowPrice, tcnud.getQty());
            result.setMarketValue(marketValue);
            sumMarketValue.add(marketValue);

            BigDecimal unrealProfit = this.calcTool.calcUnrealProfit(marketValue, tcnud.getCost());
            result.setUnrealProfit(unrealProfit);
            sumUnrealProfit.add(unrealProfit);

            resultList.add(result);
        }

        List<SumResult> sumResultList = new ArrayList<>();
        SumResult sumResult = new SumResult();
        sumResult.setStock(request.getStock());
        sumResult.setStockName(stockName);
        sumResult.setNowprice(nowPrice);
        sumResult.setSumRemainQty(remainQty);
        sumResult.setSumFee(sumFee);
        sumResult.setSumCost(sumCost);
        sumResult.setSumMarketValue(sumMarketValue);
        sumResult.setDetailList(resultList);
        sumResultList.add(sumResult);
        return sumResultList;
    }


        @Transactional(rollbackOn = Exception.class)
    public String buyStock(TransactionRequest request) throws Exception {
        // HCMIO 和 TCNUD 都新增一筆

        //確認資料都有收到
        if (request.getTradeDate().isBlank()) {
            return "Require TradeDate";
        }

        //新增明細 HCMIO
        HCMIO hcmio = addHCMIO(request);

        //新增TCNUD
        addTCNUD(hcmio);

        return "";

    }

    //新增HCMIO
//    @Transactional(rollbackOn = Exception.class)
    private HCMIO addHCMIO(TransactionRequest request) {
        HCMIO hcmio = new HCMIO();
        hcmio.setTradeDate(request.getTradeDate()); //1
        hcmio.setBranchNo(request.getBranchNo()); //2
        hcmio.setCustSeq(request.getCustSeq()); //3
        hcmio.setDocSeq(request.getDocSeq()); //4
        hcmio.setStock(request.getStock()); //5
        hcmio.setPrice(request.getPrice()); //6
        hcmio.setBstype(BSType.B); //buy //7
        hcmio.setQty(request.getQty()); //8

        BigDecimal amt = calcTool.calcAmt(request.getQty(), request.getPrice());
        hcmio.setAmt(amt); //9
        hcmio.setFee(calcTool.calcFee(amt)); //10
        hcmio.setTax(calcTool.calcTax(hcmio.getBstype())); //11
        hcmio.setStintax(BigDecimal.valueOf(0)); //固定為0 //12
        hcmio.setNetAmt(calcTool.calcNetAmt(hcmio.getBstype(), hcmio.getAmt())); //13

        String[] now = calcTool.dateTimeNow();
        String dateNow = now[0];
        String timeNow = now[1];
        hcmio.setModDate(dateNow); //14
        hcmio.setModTime(timeNow); //15
        hcmio.setModUser("HuaiChiu"); //16

        this.hcmioRepository.save(hcmio);
        return hcmio;

    }

    //新增 TCNUD
//     @Transactional(rollbackOn = Exception.class)
    private void addTCNUD(HCMIO hcmio) { //throws Exception
        TCNUD tcnud = new TCNUD();
        tcnud.setTradeDate(hcmio.getTradeDate()); //1
        tcnud.setBranchNo(hcmio.getBranchNo()); //2
        tcnud.setCustSeq(hcmio.getCustSeq()); //3
        tcnud.setDocSeq(hcmio.getDocSeq()); //4
        tcnud.setStock(hcmio.getStock()); //5
        tcnud.setPrice(hcmio.getPrice()); //6
        tcnud.setQty(hcmio.getQty()); //7

        tcnud.setRemainQty(calRemainQty(hcmio)); //8

        tcnud.setFee(hcmio.getFee()); //9
        tcnud.setCost(hcmio.getNetAmt().abs()); //cost 一定為正 //10
        tcnud.setModDate(hcmio.getModDate()); //11
        tcnud.setModTime(hcmio.getModTime()); //12
        tcnud.setModUser(hcmio.getModUser()); //13
        this.tcnudRepository.save(tcnud);
//        throw new Exception("for test");
    }

    public BigDecimal calRemainQty(HCMIO hcmio) { //要加的股票、加多少股
        TCNUD LatestTcnud = this.tcnudRepository.findLatestStock(hcmio.getStock());
        if (LatestTcnud == null) {
            return hcmio.getQty();
        } else {
            return hcmio.getQty().add(LatestTcnud.getRemainQty());
            //8 //要加上之前的(同 stock 最新一筆)
        }
    }


}
