package com.landmgmt.landbackend.dao;

import java.util.List;

import com.landmgmt.landbackend.model.LandDetails;

public interface LandDao {
    void save(LandDetails land);
    void update(LandDetails land);
    List<LandDetails> findAll();
}