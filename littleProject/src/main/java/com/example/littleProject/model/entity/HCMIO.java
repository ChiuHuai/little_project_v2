package com.example.littleProject.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(HCMIOId.class)
@Table(name = "hcmio")
public class HCMIO {

    @Id
    @Column(name = "TradeDate", nullable = false, length = 8)
    private String tradeDate;

    @Id
    @Column(name = "BranchNo", nullable = false, length = 4)
    private String branchNo;

    @Id
    @Column(name = "CustSeq", nullable = false, length = 7)
    private String custSeq;

    @Id
    @Column(name = "DocSeq", nullable = false, length = 5)
    private String docSeq;

    @Column(name = "Stock", nullable = false, length = 6)
    private String stock;


    @Enumerated(EnumType.STRING)
    @Column(name = "BsType", nullable = false, length = 1)
    private BSType bstype;

    @Column(name = "Price")
    private BigDecimal price;

    @Column(name = "Qty")
    private BigDecimal qty;

    @Column(name = "Amt")
    private BigDecimal amt;
    //private BigDecimal amt

    @Column(name = "Fee")
    private BigDecimal fee; //long

    @Column(name = "Tax")
    private BigDecimal tax; //long

    @Column(name = "StinTax")
    private BigDecimal stintax;

    @Column(name = "NetAmt")
    private BigDecimal netAmt;

    @Column(name = "ModDate", length = 8)
    private String modDate;

    @Column(name = "ModTime", length = 6)
    private String modTime;

    @Column(name = "ModUser", length = 10)
    private String modUser;


}
