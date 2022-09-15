package com.example.littleProject.controller.dto.response;

import com.example.littleProject.model.entity.MSTMB;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MSTMBResponse {
    String message;
    MSTMB mstmb;
}
