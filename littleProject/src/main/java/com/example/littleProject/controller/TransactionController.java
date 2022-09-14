package com.example.littleProject.controller;

import com.example.littleProject.controller.dto.request.TransactionRequest;
import com.example.littleProject.controller.dto.request.UnrealRequest;
import com.example.littleProject.controller.dto.response.StatusResponse;
import com.example.littleProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

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
        StatusResponse statusResponse = this.transactionService.detailsOfUnrealizedProfit(request);
        return statusResponse;
    }

    @PostMapping("/unreal/sum")
    public StatusResponse sumOfUnrealizedProfit(@Valid @RequestBody UnrealRequest request) {
        try {
            StatusResponse statusResponse = this.transactionService.sumOfUnrealizedProfit(request);

            return statusResponse;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
//            return new StatusResponse().builder().responseCode("005").message("xxx").resultList(new ArrayList<>()).build();
        }


    }

    @PostMapping("/unreal/add")
    public StatusResponse addUnreal(@Valid @RequestBody TransactionRequest request) {
        String responseMessage = null;
        try {
            StatusResponse statusResponse = this.transactionService.buyStock(request);
            return statusResponse;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/searchSettlement")
    public String searchSettlement(@RequestParam String branchNo, @RequestParam String custSeq){
        String response = this.transactionService.searchSettlement(branchNo, custSeq);
        return response;
    }

//    @ExceptionHandler(value = Exception.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
//    public StatusResponse handleException(Exception ex) {
//        StatusResponse response = new StatusResponse();
//        response.setMessage("伺服器忙碌中，請稍後嘗試");
//        response.setResultList(new ArrayList<>());
//        response.setResponseCode("005");
//        return response.builder()
//                .resultList(new ArrayList<>())
//                .responseCode("005")
//                .message("伺服器忙碌中，請稍後嘗試")
//                .build();
//    }


}
