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
@IdClass(TCNUDId.class)
@Table(name = "tcnud")
public class TCNUD {
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

    @Column(name = "Price")
    private BigDecimal price;

    @Column(name = "Qty")
    private BigDecimal qty;

    @Column(name = "RemainQty")
    private BigDecimal remainQty;

    @Column(name = "Fee")
    private BigDecimal fee;

    @Column(name = "Cost")
    private BigDecimal cost;

    @Column(name = "ModDate", length = 8)
    private String modDate;

    @Column(name = "ModTime", length = 6)
    private String modTime;

    @Column(name = "ModUser", length = 10)
    private String modUser;
}
