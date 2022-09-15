package com.example.littleProject.controller;

import com.example.littleProject.controller.dto.request.SettlementRequest;
import com.example.littleProject.controller.dto.request.TransactionRequest;
import com.example.littleProject.controller.dto.request.UnrealRequest;
import com.example.littleProject.controller.dto.response.StatusResponse;
import com.example.littleProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
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
    private Tool tool;

    @PostMapping("/unreal/detail")
    public StatusResponse detailsOfUnrealizedProfit(@Valid @RequestBody UnrealRequest request) {
        StatusResponse statusResponse = new StatusResponse();
        try {
            statusResponse = this.transactionService.detailsOfUnrealizedProfit(request);
        } catch (Exception e) {
            statusResponse.builder().responseCode("005").message("伺服器忙碌中，請稍後嘗試").resultList(new ArrayList<>()).build();
        }
        return statusResponse;
    }

    @PostMapping("/unreal/sum")
    public StatusResponse sumOfUnrealizedProfit(@Valid @RequestBody UnrealRequest request) {
        StatusResponse statusResponse = new StatusResponse();
        try {
            statusResponse = this.transactionService.sumOfUnrealizedProfit(request);
        } catch (Exception e) {
            statusResponse.builder().responseCode("005").message("伺服器忙碌中，請稍後嘗試").resultList(new ArrayList<>()).build();
        }
        return statusResponse;
    }

    @PostMapping("/unreal/add")
    public StatusResponse addUnreal(@Valid @RequestBody TransactionRequest request) {
        StatusResponse statusResponse = new StatusResponse();
        try {
            statusResponse = this.transactionService.buyStock(request);
        } catch (Exception e) {
            statusResponse.builder().responseCode("005").message("伺服器忙碌中，請稍後嘗試").resultList(new ArrayList<>()).build();
        }
        return statusResponse;
    }

    @PostMapping("/searchSettlement")
    public String searchSettlement(@RequestBody @Valid SettlementRequest request) {
        String response= "";
        try {
            response = this.transactionService.searchSettlement(request.getBranchNo(), request.getCustSeq());
        } catch (Exception e) {
            response = "伺服器忙碌中，請稍後嘗試";
        }
        return response;
    }

}
