package com.landmgmt.landbackend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Seller {
    private Long sellerId;
    private String name;
    private String contactNo;
    private String address;
    private String aadharNo;
}