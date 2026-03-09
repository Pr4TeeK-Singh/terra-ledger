import { Component, Input, OnInit, OnChanges, SimpleChanges, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';

import { Land } from '../../models/land';
import { Plot } from '../../models/plot';
import { Seller } from '../../models/seller';
import { Broker } from '../../models/broker';
import { PlotService } from '../../services/plot.service';

@Component({
  selector: 'app-plot-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './plot-panel.html',
  styleUrl: './plot-panel.css'
})
export class PlotPanelComponent implements OnInit, OnChanges, OnDestroy {

  @Input() land!: Land;

  plots: Plot[] = [];
  sellers: Seller[] = [];
  brokers: Broker[] = [];

  remainingArea = 0;
  totalArea = 0;
  usedArea = 0;

  newPlot!: Plot;
  showSellerModal = false;
  showBrokerModal = false;
  newSeller: Seller = this.emptySeller();
  newBroker: Broker = this.emptyBroker();

  editingPlotId: number | null = null;
  private snapshot: Plot | null = null;
  private sub!: Subscription;

  constructor(private plotService: PlotService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.sub = this.plotService.refresh$.subscribe(() => this.loadPlots());
    this.reset();
    this.loadPlots();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['land'] && !changes['land'].firstChange) {
      this.reset();
      this.loadPlots();
    }
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }

  loadPlots(): void {
    if (!this.land?.landId) return;
    this.plotService.getByLandId(this.land.landId!).subscribe({
      next: plots => {
        this.plots = plots;
        this.extractSellersAndBrokers();
        this.calcRemaining();
        this.cdr.markForCheck();
      }
    });
  }

  extractSellersAndBrokers(): void {
    const sm = new Map<number, Seller>();
    const bm = new Map<number, Broker>();
    this.plots.forEach(p => {
      if (p.seller?.sellerId) sm.set(p.seller.sellerId, p.seller);
      if (p.broker?.brokerId) bm.set(p.broker.brokerId, p.broker);
    });
    this.sellers = Array.from(sm.values());
    this.brokers  = Array.from(bm.values());
  }

  calcRemaining(): void {
    this.totalArea     = (this.land.lengthInSqft || 0) * (this.land.widthInSqft || 0);
    this.usedArea      = this.plots.reduce((s, p) => s + ((p.landLength as any) * (p.landWidth as any)), 0);
    this.remainingArea = this.totalArea - this.usedArea;
  }

  reset(): void {
    this.newPlot = {
      gaataNo: this.land?.gaataNumber || '', landId: this.land?.landId || 0,
      plotNo: '', landLength: 0, landWidth: 0, sellRate: 0, totalAmount: 0,
      status: 'AVAILABLE', sellerId: undefined, seller: null, brokerId: undefined, broker: null
    };
  }

  calculate(): void {
    this.newPlot.totalAmount = (this.newPlot.landLength as any || 0) * (this.newPlot.landWidth as any || 0) * (this.newPlot.sellRate as any || 0);
  }

  calculateEdit(plot: Plot): void {
    plot.totalAmount = (plot.landLength as any || 0) * (plot.landWidth as any || 0) * (plot.sellRate as any || 0);
  }

  save(): void {
    this.plotService.save({ ...this.newPlot, landId: this.land.landId! }).subscribe({
      next: () => { this.reset(); this.loadPlots(); this.plotService.triggerRefresh(); }
    });
  }

  editPlot(plot: Plot): void { this.editingPlotId = plot.plotId!; this.snapshot = JSON.parse(JSON.stringify(plot)); }

  cancelEdit(): void {
    if (this.snapshot) {
      const idx = this.plots.findIndex(p => p.plotId === this.snapshot!.plotId);
      if (idx > -1) this.plots[idx] = this.snapshot;
    }
    this.editingPlotId = null; this.snapshot = null; this.cdr.markForCheck();
  }

  savePlot(plot: Plot): void {
    this.plotService.update(plot).subscribe({
      next: () => { this.editingPlotId = null; this.snapshot = null; this.loadPlots(); }
    });
  }

  openSellerModal(): void  { this.showSellerModal = true; }
  closeSellerModal(): void { this.showSellerModal = false; this.newSeller = this.emptySeller(); }
  openBrokerModal(): void  { this.showBrokerModal = true; }
  closeBrokerModal(): void { this.showBrokerModal = false; this.newBroker = this.emptyBroker(); }

  attachSeller(): void { this.newPlot.sellerId = undefined; this.newPlot.seller = { ...this.newSeller }; this.closeSellerModal(); }
  attachBroker(): void { this.newPlot.brokerId = undefined; this.newPlot.broker = { ...this.newBroker }; this.closeBrokerModal(); }

  onSellerSelect(id: number): void { if (id) this.newPlot.seller = null; }
  onBrokerSelect(id: number): void { if (id) this.newPlot.broker = null; }

  emptySeller(): Seller { return { name: '', contactNo: '', address: '', aadharNo: '' }; }
  emptyBroker(): Broker { return { name: '', contactNo: '', address: '', aadharNo: '' }; }
}