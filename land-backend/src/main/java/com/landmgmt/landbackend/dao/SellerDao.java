package com.landmgmt.landbackend.dao;

import java.util.List;

import com.landmgmt.landbackend.model.Seller;

public interface SellerDao {
    Long save(Seller seller);
    List<Seller> findAll();
}