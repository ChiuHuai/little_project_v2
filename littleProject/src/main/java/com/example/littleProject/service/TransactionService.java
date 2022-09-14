package com.example.littleProject.service;

import com.example.littleProject.controller.dto.request.TransactionRequest;
import com.example.littleProject.controller.dto.request.UnrealRequest;
import com.example.littleProject.controller.dto.response.Result;
import com.example.littleProject.controller.dto.response.ResultResponse;
import com.example.littleProject.controller.dto.response.StatusResponse;
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
import java.util.stream.Collectors;

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

    public StatusResponse detailsOfUnrealizedProfit(UnrealRequest request) {
        List<Result> resultList;

        if (null == request.getStock() || request.getStock().isBlank()) {
            List<TCNUD> allTCNUDList = this.tcnudRepository.findByCustSeqAndBranchNo(request.getCustSeq(), request.getBranchNo());
            resultList = TCNUDToResultList(allTCNUDList);

        } else if (this.mstmbRepository.findByStock(request.getStock()) == null) { //買了之後倒
            //mstmb 沒有 -> 001 - 查無結果（message請回覆「查無符合資料」）
            return statusResponseBuilder("001", "");
        } else {
            List<TCNUD> TCNUDList = this.tcnudRepository.findByStockAndCustSeqAndBranchNo(
                    request.getStock(), request.getCustSeq(), request.getBranchNo());

            if (TCNUDList.size() == 0) { //此人沒買該股票
                return statusResponseBuilder("001", "");
            }
            resultList = TCNUDToResultList(TCNUDList);
        }

        BigDecimal max = request.getMax();
        BigDecimal min = request.getMin();
        if (max != null && min != null) {
            if (max.compareTo(min) < 0) {
                return statusResponseBuilder("002", "min should not greater than max");
            }
        } else if (max != null || min != null) {
            List<? extends ResultResponse> rangeOfProfitMargin = this.findRangeOfProfitMargin(min, max, resultList);
            if (rangeOfProfitMargin.size() == 0) {
                return statusResponseBuilder("001", "");
            }
            resultList = rangeOfProfitMargin.stream().map(e -> (Result) e).collect(Collectors.toList());
        }

        return statusResponseBuilder("000", "", resultList);
    }


    public StatusResponse sumOfUnrealizedProfit(UnrealRequest request) {
        //StatusResponse 內 resultList == List<SumResult>, SumResult 內 detailList == list<Result>
        //當 request.getStock() 為空，列出同 branchNo,custSeq  全部
        // -> 每一種stock為一個SumResult -> 找同個人買多少種股票

        List<SumResult> sumResultList;
        List<String> stockList = new ArrayList<>();

        //當 request 沒有提供 stock
        if (null == request.getStock() || request.getStock().isBlank()) {
            //找出同個人買多少股票
            List<TCNUD> allTCNUDList = this.tcnudRepository.findByCustSeqAndBranchNo(request.getCustSeq(), request.getBranchNo());
            //取出股票種類
            stockList = allTCNUDList.stream().map(e -> e.getStock())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());

            sumResultList = stockToSumResultList(request, stockList);
        } else {
            List<TCNUD> TCNUDList = this.tcnudRepository.findByStockAndCustSeqAndBranchNo(
                    request.getStock(), request.getCustSeq(), request.getBranchNo());

            if (TCNUDList.size() == 0) { //此人沒買該股票
                return statusResponseBuilder("001", "");

            }

            stockList.add(request.getStock());
            sumResultList = stockToSumResultList(request, stockList);
        }


        BigDecimal max = request.getMax();
        BigDecimal min = request.getMin();
        if (max != null && min != null) {
            if (max.compareTo(min) < 0) {
                return statusResponseBuilder("002", "min should not greater than max");
            }
        } else if (max != null || min != null) {
            List<? extends ResultResponse> rangeOfProfitMargin = this.findRangeOfProfitMargin(min, max, sumResultList);
            if (rangeOfProfitMargin.size() == 0) {
                return statusResponseBuilder("001", "");
            }
            sumResultList = rangeOfProfitMargin.stream().map(e -> (SumResult) e).collect(Collectors.toList());
        }

        return statusResponseBuilder("000", "", sumResultList);

    }


    @Transactional(rollbackOn = Exception.class)
    public StatusResponse buyStock(TransactionRequest request) { //throws Exception
        // HCMIO 和 TCNUD 都新增一筆

        //1.書號相同無法新增/////////////////////////////////
        //2. tradeDate 不同, 其他同 可以新增嗎
        //if(xxx&&xxx&&xxx&&xxx){不能加}

        //新增明細 HCMIO
        HCMIO hcmio = addHCMIO(request);

        //新增TCNUD
        addTCNUD(hcmio);

        //statusResponse
        Result result = new Result();

        //找到相同書號，加入資料到 statusResponse 的 ResultList //findby 4 conditional
        TCNUD tcnud = this.tcnudRepository.findByDocSeq(request.getDocSeq());
        result.setTradeDate(tcnud.getTradeDate());
        result.setDocSeq(tcnud.getDocSeq());
        result.setStock(tcnud.getStock());

        //不確定
        BigDecimal buyPrice = tcnud.getPrice().setScale(2, RoundingMode.HALF_UP); //在一開始時維持後兩位
        result.setBuyprice(buyPrice);
        result.setQty(tcnud.getQty());
        result.setRemainQty(tcnud.getRemainQty());
        result.setFee(tcnud.getFee());
        BigDecimal cost = tcnud.getCost();
        result.setCost(cost);
        MSTMB mstmb = this.mstmbRepository.findByStock(request.getStock());
        result.setStockName(mstmb.getStockName());

        //要防沒資料
        BigDecimal nowPrice = mstmb.getCurPrice().setScale(2, RoundingMode.HALF_UP);
        result.setNowprice(nowPrice); // from MSTMB

        BigDecimal marketValue = this.calcTool.calcMarketValue(nowPrice, tcnud.getQty());
        BigDecimal unrealProfit = this.calcTool.calcUnrealProfit(marketValue, tcnud.getCost());

        result.setMarketValue(marketValue.setScale(0, RoundingMode.HALF_UP));
        result.setUnrealProfit(unrealProfit.setScale(0, RoundingMode.HALF_UP));

        //        profitMargin
        String profitMargin = this.calcTool.calcProfitMargin(unrealProfit, cost);
        result.setProfitMargin(profitMargin);

        //注意四捨五入
        List<Result> resultList = new ArrayList<>();
        resultList.add(result);
        System.out.println(result.getStock());
        System.out.println(resultList.get(0).getFee());
        return statusResponseBuilder("000", "", resultList);
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

        tcnud.setRemainQty(hcmio.getQty()); //8

        tcnud.setFee(hcmio.getFee()); //9
        tcnud.setCost(hcmio.getNetAmt().abs()); //cost 一定為正 //10
        tcnud.setModDate(hcmio.getModDate()); //11
        tcnud.setModTime(hcmio.getModTime()); //12
        tcnud.setModUser(hcmio.getModUser()); //13
        this.tcnudRepository.save(tcnud);
//        throw new Exception("for test");
    }

    public StatusResponse statusResponseBuilder(String responseCode, String message, List<? extends ResultResponse>... resultList) {
        StatusResponse.StatusResponseBuilder builder = new StatusResponse().builder();

        if ("001".equals(responseCode)) {
            return builder.message("查無符合資料")
                    .responseCode(responseCode)
                    .resultList(new ArrayList<>())
                    .build();
        } else if ("002".equals(responseCode)) {
            return builder.message(message)
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

    public List<Result> TCNUDToResultList(List<TCNUD> TCNUDList) {
        List<Result> resultList = new ArrayList<>();

        Result result;
        for (TCNUD tcnud : TCNUDList) {
            MSTMB mstmb1 = this.mstmbRepository.findByStock(tcnud.getStock());
            BigDecimal nowPrice = mstmb1.getCurPrice(); //取得現價
            String stockName = mstmb1.getStockName(); //取得 stockName
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
            //成本
            BigDecimal cost = tcnud.getCost();
            result.setCost(cost.setScale(0, RoundingMode.HALF_UP));//

            BigDecimal marketValue = this.calcTool.calcMarketValue(nowPrice, tcnud.getQty());
            result.setMarketValue(marketValue.setScale(0, RoundingMode.HALF_UP));
            BigDecimal unrealProfit = this.calcTool.calcUnrealProfit(marketValue, cost);
            result.setUnrealProfit(unrealProfit.setScale(0, RoundingMode.HALF_UP));

            String profitMargin = this.calcTool.calcProfitMargin(unrealProfit, cost);
            result.setProfitMargin(profitMargin);

            resultList.add(result);


        }
        return resultList;
    }

    public List<SumResult> stockToSumResultList(UnrealRequest request, List<String> stockList) {
        List<SumResult> sumResultList = new ArrayList<>();
        List<Result> resultList;
        Result result;

        for (String stock : stockList) {
            MSTMB mstmb = this.mstmbRepository.findByStock(stock);
            BigDecimal nowPrice = mstmb.getCurPrice().setScale(2, RoundingMode.HALF_UP); //取得現價
            String stockName = mstmb.getStockName(); //取得 stockName

            List<TCNUD> TCNUDList = this.tcnudRepository.findByStockAndCustSeqAndBranchNo(
                    stock, request.getCustSeq(), request.getBranchNo());

            SumResult sumResult = new SumResult();
            resultList = new ArrayList<>();

            BigDecimal sumRemainQty = new BigDecimal(0);
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

                BigDecimal remainQty = tcnud.getRemainQty();
                result.setRemainQty(remainQty);
                sumRemainQty = sumRemainQty.add(remainQty);

                BigDecimal fee = tcnud.getFee();
                result.setFee(fee);
                sumFee = sumFee.add(fee);

                BigDecimal cost = tcnud.getCost();
                result.setCost(cost.setScale(0, RoundingMode.HALF_UP)); //後處理小數
                sumCost = sumCost.add(cost);

                BigDecimal marketValue = this.calcTool.calcMarketValue(nowPrice, tcnud.getQty());
                result.setMarketValue(marketValue.setScale(0, RoundingMode.HALF_UP));
                sumMarketValue = sumMarketValue.add(marketValue);

                BigDecimal unrealProfit = this.calcTool.calcUnrealProfit(marketValue, tcnud.getCost());
                result.setUnrealProfit(unrealProfit.setScale(0, RoundingMode.HALF_UP));
                sumUnrealProfit = sumUnrealProfit.add(unrealProfit);

                String profitMargin = this.calcTool.calcProfitMargin(unrealProfit, cost);
                result.setProfitMargin(profitMargin);
                resultList.add(result);
            }
            sumResult.setStock(stock);
            sumResult.setStockName(stockName);
            sumResult.setNowprice(nowPrice);
            sumResult.setSumRemainQty(sumRemainQty);
            sumResult.setSumFee(sumFee);
            sumResult.setSumCost(sumCost.setScale(0, RoundingMode.HALF_UP));
            sumResult.setSumMarketValue(sumMarketValue.setScale(0, RoundingMode.HALF_UP));
            sumResult.setSumUnrealProfit(sumUnrealProfit.setScale(0, RoundingMode.HALF_UP));

            String sumProfitMargin = this.calcTool.calcProfitMargin(sumUnrealProfit, sumCost);
            sumResult.setSumProfitMargin(sumProfitMargin);

            sumResult.setDetailList(resultList);
            sumResultList.add(sumResult);
        }
        return sumResultList;
    }

    //查詢獲利率區間
    public List<? extends ResultResponse> findRangeOfProfitMargin(BigDecimal min, BigDecimal max, List<? extends ResultResponse> resultResponseList) {
        if (resultResponseList.get(0) instanceof Result) {
            List<Result> resultList = resultResponseList.stream().map(resultResponse -> (Result) resultResponse).collect(Collectors.toList());

            return resultList.stream().filter(e ->
                            this.profitMarginFilter(min, max, e.getProfitMargin()))
                    .collect(Collectors.toList());
        } else if (resultResponseList.get(0) instanceof SumResult) {
            List<SumResult> sumResultList = resultResponseList.stream().map(resultResponse -> (SumResult) resultResponse).collect(Collectors.toList());
            return sumResultList.stream().filter(e -> this.profitMarginFilter(min, max, e.getSumProfitMargin()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    //查詢獲利率區間
    public boolean profitMarginFilter(BigDecimal min, BigDecimal max, String profitMargin) {
        BigDecimal profitMarginBD = new BigDecimal(profitMargin.replace("%", "").trim());
        if (max != null && min != null) {
            if (max.compareTo(min) == 0) { //max == min
                return profitMarginBD.compareTo(max) == 0;
            } else {
                return (profitMarginBD.compareTo(max) == -1) && (profitMarginBD.compareTo(min) == 1);
            }
        } else if (min == null && max != null) {  //only max
            return profitMarginBD.compareTo(max) == -1;

        } else if (max == null && min != null) {  //only min
            return profitMarginBD.compareTo(min) == 1;
        } else {
            return false;
        }
    }


}
