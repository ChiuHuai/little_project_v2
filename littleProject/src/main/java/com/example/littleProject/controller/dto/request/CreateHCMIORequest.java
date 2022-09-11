package com.example.littleProject.controller.dto.request;

import com.example.littleProject.model.entity.BSType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateHCMIORequest {
    private String tradeDate;
    private String branchNo;
    private String custSeq;
    private String docSeq;
    private String stock;
    private Double price;
    private int qty;
}
