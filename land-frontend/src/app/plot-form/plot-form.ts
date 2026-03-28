import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Land } from '../models/land';
import { Plot } from '../models/plot';
import { PlotService } from '../services/plot.service';

@Component({
  selector: 'app-plot-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './plot-form.html',
  styleUrl: './plot-form.css'
})
export class PlotFormComponent {

  @Input() land!: Land;
  @Output() plotSaved = new EventEmitter<void>();

  plot: Plot = this.emptyPlot();

  constructor(private plotService: PlotService) {}

  calculate(): void {
    this.plot.totalAmount = (this.plot.landLength || 0) * (this.plot.landWidth || 0) * (this.plot.sellRate || 0);
  }

  save(): void {
    this.plot.landId = this.land.landId!;
    this.plotService.save(this.plot).subscribe({
      next: () => {
        this.plot = this.emptyPlot();
        this.plotSaved.emit();
      }
    });
  }

  emptyPlot(): Plot {
    return {
      gaataNo: '', landId: 0, plotNo: '',
      landLength: 0, landWidth: 0, sellRate: 0,
      totalAmount: 0, status: 'AVAILABLE',
      seller: { name: '', contactNo: '', address: '', aadharNo: '' },
      broker: { name: '', contactNo: '', address: '', aadharNo: '' }
    };
  }
}