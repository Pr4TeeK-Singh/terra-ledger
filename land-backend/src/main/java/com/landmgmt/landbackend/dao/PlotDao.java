package com.landmgmt.landbackend.dao;

import java.util.List;

import com.landmgmt.landbackend.model.Plot;

public interface PlotDao {
    void save(Plot plot);
    void update(Plot plot);
    List<Plot> findByLandId(Long landId);
}