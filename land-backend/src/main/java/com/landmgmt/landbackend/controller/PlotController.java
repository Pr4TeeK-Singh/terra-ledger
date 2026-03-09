package com.landmgmt.landbackend.controller;

import java.util.List;

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

    private final PlotService plotService;

    public PlotController(PlotService plotService) {
        this.plotService = plotService;
    }

    @PostMapping
    public ResponseEntity<?> savePlot(@RequestBody Plot plot) {
        plotService.save(plot);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{plotId}")
    public ResponseEntity<?> updatePlot(@PathVariable Long plotId, @RequestBody Plot plot) {
        plot.setPlotId(plotId);
        plotService.update(plot);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/land/{landId}")
    public List<Plot> getByLandId(@PathVariable Long landId) {
        return plotService.getByLandId(landId);
    }
}