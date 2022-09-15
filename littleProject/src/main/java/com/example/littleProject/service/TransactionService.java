package com.example.littleProject.service;

import com.example.littleProject.controller.dto.request.TransactionRequest;
import com.example.littleProject.controller.dto.request.UnrealRequest;
import com.example.littleProject.controller.dto.response.Result;
import com.example.littleProject.controller.dto.response.ResultResponse;
import com.example.littleProject.controller.dto.response.StatusResponse;
import com.example.littleProject.controller.dto.response.SumResult;
import com.example.littleProject.model.HCMIORepository;
import com.example.littleProject.model.HolidayRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private HolidayRepository holidayRepository;

    @Autowired
    private Tool tool;

    public StatusResponse detailsOfUnrealizedProfit(UnrealRequest request) {
        List<Result> resultList = new ArrayList<>();

        if (null == request.getStock() || request.getStock().isBlank()) {
            List<TCNUD> allTCNUDList = this.tcnudRepository.findByCustSeqAndBranchNo(request.getCustSeq(), request.getBranchNo());
            //取出股票種類
            List<String> stockList = allTCNUDList.stream().map(e -> e.getStock())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            for (String stock : stockList) {
                resultList.addAll(TCNUDToResultList(request.getBranchNo(), request.getCustSeq(), stock));
            }

        } else if (this.mstmbRepository.findByStock(request.getStock()) == null) { //買了之後倒
            //mstmb 沒有 -> 001 - 查無結果（message請回覆「查無符合資料」）
            return this.tool.statusResponseBuilder("001", "");
        } else {
            List<TCNUD> TCNUDList = this.tcnudRepository.findByStockAndCustSeqAndBranchNo(
                    request.getStock(), request.getCustSeq(), request.getBranchNo());

            if (TCNUDList.size() == 0) return this.tool.statusResponseBuilder("001", "");//此人沒買該股票
            resultList = TCNUDToResultList(request.getBranchNo(), request.getCustSeq(), request.getStock());
        }

        BigDecimal max = request.getMax();
        BigDecimal min = request.getMin();
        if (max != null && min != null && max.compareTo(min) < 0)
            return this.tool.statusResponseBuilder("002", "min should not greater than max");

        if (max != null || min != null) {
            List<? extends ResultResponse> rangeOfProfitMargin = this.findRangeOfProfitMargin(min, max, resultList);
            if (rangeOfProfitMargin.size() == 0) return this.tool.statusResponseBuilder("001", "");
            resultList = rangeOfProfitMargin.stream().map(e -> (Result) e).collect(Collectors.toList());
        }

        return this.tool.statusResponseBuilder("000", "", resultList);
    }


    public StatusResponse sumOfUnrealizedProfit(UnrealRequest request) {
        //StatusResponse 內 resultList == List<SumResult>, SumResult 內 detailList == list<Result>
        //當 request.getStock() 為空，列出同 branchNo, custSeq  全部
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
                return this.tool.statusResponseBuilder("001", "");
            }

            stockList.add(request.getStock());
            sumResultList = stockToSumResultList(request, stockList);
        }


        BigDecimal max = request.getMax();
        BigDecimal min = request.getMin();

        if (max != null && min != null && max.compareTo(min) < 0)
            return this.tool.statusResponseBuilder("002", "min should not greater than max");

        if (max != null || min != null) {
            List<? extends ResultResponse> rangeOfProfitMargin = this.findRangeOfProfitMargin(min, max, sumResultList);
            if (rangeOfProfitMargin.size() == 0)
                return this.tool.statusResponseBuilder("001", "");
            sumResultList = rangeOfProfitMargin.stream().map(e -> (SumResult) e).collect(Collectors.toList());
        }
        return this.tool.statusResponseBuilder("000", "", sumResultList);

    }


    @Transactional(rollbackOn = Exception.class)
    public StatusResponse buyStock(TransactionRequest request) {
        // HCMIO 和 TCNUD 都新增一筆

        List<HCMIO> hcmioList = this.hcmioRepository.findByTradeDateAndBranchNoAndCustSeqAndDocSeq(request.getTradeDate(),
                request.getBranchNo(), request.getCustSeq(), request.getDocSeq());
        if (hcmioList.size() > 0)
            return this.tool.statusResponseBuilder("002", "already exist in HCMIO, cannot create.");
        //新增明細 HCMIO
        HCMIO hcmio = addHCMIO(request);

        //新增TCNUD
        addTCNUD(hcmio);

        //statusResponse
        Result result = new Result();

        //找到剛加的那筆，加入資料到 statusResponse 的 ResultList //findby 4 conditional
        TCNUD tcnud = this.tcnudRepository.findByTradeDateAndBranchNoAndCustSeqAndDocSeq(request.getTradeDate(),
                request.getBranchNo(), request.getCustSeq(), request.getDocSeq());
        result.setTradeDate(tcnud.getTradeDate());
        result.setDocSeq(tcnud.getDocSeq());
        result.setStock(tcnud.getStock());

        BigDecimal buyPrice = tcnud.getPrice().setScale(2, RoundingMode.HALF_UP);
        result.setBuyprice(buyPrice);
        result.setQty(tcnud.getQty());
        result.setRemainQty(tcnud.getRemainQty());
        result.setFee(tcnud.getFee());
        BigDecimal cost = tcnud.getCost();
        result.setCost(cost.setScale(0, RoundingMode.HALF_UP));

        MSTMB mstmb = this.mstmbRepository.findByStock(request.getStock());
        if (null == mstmb) { //要防 mstmb 沒資料
            return this.tool.statusResponseBuilder("002", "this stock is not exist in MSTMB.");
        }
        result.setStockName(mstmb.getStockName());

        BigDecimal nowPrice = mstmb.getCurPrice().setScale(2, RoundingMode.HALF_UP);
        result.setNowprice(nowPrice); // from MSTMB

        BigDecimal marketValue = this.tool.calcMarketValue(nowPrice, tcnud.getQty());
        BigDecimal unrealProfit = this.tool.calcUnrealProfit(marketValue, tcnud.getCost());

        result.setMarketValue(marketValue.setScale(0, RoundingMode.HALF_UP));
        result.setUnrealProfit(unrealProfit.setScale(0, RoundingMode.HALF_UP));

        //profitMargin
        String profitMargin = this.tool.calcProfitMargin(unrealProfit, cost);
        result.setProfitMargin(profitMargin);

        List<Result> resultList = new ArrayList<>();
        resultList.add(result);
        return this.tool.statusResponseBuilder("000", "", resultList);
    }

    //新增HCMIO
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

        BigDecimal amt = tool.calcAmt(request.getQty(), request.getPrice());
        hcmio.setAmt(amt); //9
        hcmio.setFee(tool.calcFee(amt)); //10
        hcmio.setTax(tool.calcTax(hcmio.getBstype())); //11
        hcmio.setStintax(BigDecimal.valueOf(0)); //固定為0 //12
        hcmio.setNetAmt(tool.calcNetAmt(hcmio.getBstype(), hcmio.getAmt())); //13

        String[] now = tool.dateTimeNow();
        String dateNow = now[0];
        String timeNow = now[1];
        hcmio.setModDate(dateNow); //14
        hcmio.setModTime(timeNow); //15
        hcmio.setModUser("HuaiChiu"); //16

        this.hcmioRepository.save(hcmio);
        return hcmio;

    }

    //新增 TCNUD
    private void addTCNUD(HCMIO hcmio) {
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
    }

    public String searchSettlement(String branchNo, String custSeq) {

        //convert String to LocalDate
//        String dateTest = "2022-09-12"; // 2022-09-12 -> 2022-09-07 放假三天 測試用
//        LocalDate localDate = LocalDate.parse(dateTest);
        String message = "";

        if ("" == branchNo.trim() || "" == custSeq) {
            return "參數不得為空";
        }

        LocalDate today = LocalDate.now();
        int todayOfWeek = today.getDayOfWeek().getValue(); //Monday -> 1

        if (6 == todayOfWeek || 7 == todayOfWeek) return message = "假日無交割金";

        //如果工作日 +1
        int countDay = 0;
        String date = "";
        LocalDate tempDate = today;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        while (countDay < 2) {
            tempDate = tempDate.minusDays(1);
            int dayOfWeek = tempDate.getDayOfWeek().getValue();
            date = tempDate.format(formatter);

            String Holiday = this.holidayRepository.findByHoliday(date);
            if (null == Holiday && 6 != dayOfWeek && 7 != dayOfWeek) {
                countDay++;
            }
        }

        List<TCNUD> tcnudList = this.tcnudRepository.findByTradeDateAndBranchNoAndCustSeq(date, branchNo, custSeq);
        if (tcnudList.isEmpty()) return message = "無股票需付款";//顧客沒買

        BigDecimal totalPay = tcnudList.stream().map(e -> e.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
        message = "共需付款：" + totalPay.setScale(0, RoundingMode.HALF_UP).toString();
        return message;
    }

    public List<Result> TCNUDToResultList(String branchNo, String custSeq, String stock) {
        List<Result> resultList = new ArrayList<>();

        List<TCNUD> tcnudList = tcnudRepository.findByStockAndCustSeqAndBranchNo(stock, custSeq, branchNo);
        Result result;
        for (TCNUD tcnud : tcnudList) {
            MSTMB mstmb = this.mstmbRepository.findByStock(tcnud.getStock());
            BigDecimal nowPrice = mstmb.getCurPrice(); //取得現價
            String stockName = mstmb.getStockName(); //取得 stockName
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

            result.setMarketValue(this.tool.calcMarketValue(nowPrice, tcnud.getQty()).setScale(0, RoundingMode.HALF_UP));
            BigDecimal unrealProfit = this.tool.calcUnrealProfit(result.getMarketValue(), cost);
            result.setUnrealProfit(unrealProfit.setScale(0, RoundingMode.HALF_UP));

            result.setProfitMargin(this.tool.calcProfitMargin(unrealProfit, cost));

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
            resultList = TCNUDToResultList(request.getBranchNo(), request.getCustSeq(), stock);

            BigDecimal sumRemainQty = resultList.stream().map(e -> e.getRemainQty()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumFee = resultList.stream().map(e -> e.getFee()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumCost = resultList.stream().map(e -> e.getCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumMarketValue = resultList.stream().map(e -> e.getMarketValue()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal sumUnrealProfit = resultList.stream().map(e -> e.getUnrealProfit()).reduce(BigDecimal.ZERO, BigDecimal::add);

            sumResult.setStock(stock);
            sumResult.setStockName(stockName);
            sumResult.setNowprice(nowPrice);
            sumResult.setSumRemainQty(sumRemainQty);
            sumResult.setSumFee(sumFee);
            sumResult.setSumCost(sumCost.setScale(0, RoundingMode.HALF_UP));
            sumResult.setSumMarketValue(sumMarketValue.setScale(0, RoundingMode.HALF_UP));
            sumResult.setSumUnrealProfit(sumUnrealProfit.setScale(0, RoundingMode.HALF_UP));

            String sumProfitMargin = this.tool.calcProfitMargin(sumUnrealProfit, sumCost);
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
            return (max.compareTo(min) == 0) ? profitMarginBD.compareTo(max) == 0 : (profitMarginBD.compareTo(max) < 0) && (profitMarginBD.compareTo(min) > 0);
        } else if (min == null && max != null) {  //only max
            return profitMarginBD.compareTo(max) == -1;

        } else if (max == null && min != null) {  //only min
            return profitMarginBD.compareTo(min) == 1;
        } else {
            return false;
        }
    }


}
