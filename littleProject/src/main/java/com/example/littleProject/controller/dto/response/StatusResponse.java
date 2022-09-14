package com.example.littleProject.controller.dto.response;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class StatusResponse {
    private List<? extends ResultResponse> resultList; //Result
    private String responseCode;
    private String message;

//    static void enumerateResultList(List<? extends ResultResponse> resultList) {
//        for (ResultResponse r : resultList)
//            r.
//    }
}
