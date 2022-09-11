package com.example.littleProject.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mstmb")
public class MSTMB {
    @Id
    @Column(name = "Stock", nullable = false, length = 6)
    private String stock;

    @Column(name = "StockName", nullable = false, length = 20)
    private String stockName;

    @Column(name = "MarketType")
    private Character marketType;

    @Column(name = "CurPrice")
    private BigDecimal curPrice;

    @Column(name = "RefPrice")
    private BigDecimal refPrice;

    @Column(name = "ModDate", length = 8)
    private String modDate;

    @Column(name = "ModTime", length = 6)
    private String modTime;

    @Column(name = "ModUser", length = 10)
    private String modUser;
}

