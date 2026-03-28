package com.landmgmt.landbackend.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.landmgmt.landbackend.model.Plot;
import com.landmgmt.landbackend.service.PlotService;

@RestController
@RequestMapping("/api/plots")
@CrossOrigin(origins = "http://localhost:4200")
public class PlotController {

    private static final Logger log = LoggerFactory.getLogger(PlotController.class);

    private final PlotService plotService;

    public PlotController(PlotService plotService) {
        this.plotService = plotService;
    }

    @PostMapping
    public ResponseEntity<?> savePlot(@RequestBody Plot plot) {
        log.info("Saving plot: plotNo={}, landId={}", plot.getPlotNo(), plot.getLandId());
        plotService.save(plot);
        log.info("Plot saved successfully");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{plotId}")
    public ResponseEntity<?> updatePlot(@PathVariable Long plotId, @RequestBody Plot plot) {
        log.info("Updating plot id={}", plotId);
        plot.setPlotId(plotId);
        plotService.update(plot);
        log.info("Plot id={} updated successfully", plotId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/land/{landId}")
    public List<Plot> getByLandId(@PathVariable Long landId) {
        log.info("Fetching plots for landId={}", landId);
        List<Plot> plots = plotService.getByLandId(landId);
        log.info("Returned {} plots for landId={}", plots.size(), landId);
        return plots;
    }
}