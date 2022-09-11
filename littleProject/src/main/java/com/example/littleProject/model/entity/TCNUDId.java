package com.example.littleProject.model.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TCNUDId implements Serializable {
    private String tradeDate;
    private String branchNo;
    private String custSeq;
    private String docSeq;
}
