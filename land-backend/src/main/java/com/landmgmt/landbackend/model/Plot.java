package com.landmgmt.landbackend.model;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Plot {

    private Long plotId;
    private String gaataNo;
    private Long landId;
    private String plotNo;
    private BigDecimal landLength;
    private BigDecimal landWidth;
    private BigDecimal sellRate;
    private BigDecimal totalAmount;
    private String status;

    private Long sellerId;
    private Seller seller;

    private Long brokerId;
    private Broker broker;
}