package com.landmgmt.landbackend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.landmgmt.landbackend.dao.BrokerDao;
import com.landmgmt.landbackend.dao.PlotDao;
import com.landmgmt.landbackend.dao.SellerDao;
import com.landmgmt.landbackend.model.Broker;
import com.landmgmt.landbackend.model.Plot;
import com.landmgmt.landbackend.model.Seller;

@Service
public class PlotService {

    private final PlotDao plotDao;
    private final SellerDao sellerDao;
    private final BrokerDao brokerDao;

    public PlotService(PlotDao plotDao, SellerDao sellerDao, BrokerDao brokerDao) {
        this.plotDao = plotDao;
        this.sellerDao = sellerDao;
        this.brokerDao = brokerDao;
    }

    public void save(Plot plot) {
        plot.setSellerId(resolveSeller(plot.getSellerId(), plot.getSeller()));
        plot.setBrokerId(resolveBroker(plot.getBrokerId(), plot.getBroker()));
        calcTotal(plot);
        if (plot.getStatus() == null || plot.getStatus().isBlank()) {
            plot.setStatus("AVAILABLE");
        }
        plotDao.save(plot);
    }

    public void update(Plot plot) {
        plot.setSellerId(resolveSeller(plot.getSellerId(), plot.getSeller()));
        plot.setBrokerId(resolveBroker(plot.getBrokerId(), plot.getBroker()));
        calcTotal(plot);
        plotDao.update(plot);
    }

    public List<Plot> getByLandId(Long landId) {
        return plotDao.findByLandId(landId);
    }

    private void calcTotal(Plot plot) {
        if (plot.getLandLength() != null && plot.getLandWidth() != null && plot.getSellRate() != null) {
            plot.setTotalAmount(
                plot.getLandLength()
                    .multiply(plot.getLandWidth())
                    .multiply(plot.getSellRate())
            );
        }
    }

    private Long resolveSeller(Long sellerId, Seller seller) {
        if (sellerId != null && sellerId > 0) return sellerId;
        if (seller != null && seller.getName() != null && !seller.getName().isBlank()) {
            return sellerDao.save(seller);
        }
        return null;
    }

    private Long resolveBroker(Long brokerId, Broker broker) {
        if (brokerId != null && brokerId > 0) return brokerId;
        if (broker != null && broker.getName() != null && !broker.getName().isBlank()) {
            return brokerDao.save(broker);
        }
        return null;
    }
}