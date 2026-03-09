import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';

import { Land } from '../../models/land';
import { LandService } from '../../services/land.service';
import { PlotPanelComponent } from '../plot-panel/plot-panel';

@Component({
  selector: 'app-land-list',
  standalone: true,
  imports: [CommonModule, FormsModule, PlotPanelComponent],
  templateUrl: './land-list.html',
  styleUrl: './land-list.css'
})
export class LandListComponent implements OnInit, OnDestroy {

  lands: Land[] = [];
  editingLandId: number | null = null;
  selectedLand: Land | null = null;
  private snapshot: Land | null = null;
  private sub!: Subscription;

  constructor(private landService: LandService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.sub = this.landService.refresh$.subscribe(() => this.loadLands());
    this.loadLands();
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }

  loadLands(): void {
    this.landService.getAllLands().subscribe({
      next: lands => {
        this.lands = lands;
        if (this.selectedLand) {
          this.selectedLand = lands.find(l => l.landId === this.selectedLand!.landId) || null;
        }
        this.cdr.markForCheck();
      }
    });
  }

  selectLand(land: Land): void {
    if (this.editingLandId) return;
    this.selectedLand = this.selectedLand?.landId === land.landId ? null : land;
  }

  editLand(land: Land, event: Event): void {
    event.stopPropagation();
    this.editingLandId = land.landId!;
    this.snapshot = JSON.parse(JSON.stringify(land));
  }

  cancelEdit(event: Event): void {
    event.stopPropagation();
    if (this.snapshot) {
      const idx = this.lands.findIndex(l => l.landId === this.snapshot!.landId);
      if (idx > -1) this.lands[idx] = this.snapshot;
    }
    this.editingLandId = null;
    this.snapshot = null;
    this.cdr.markForCheck();
  }

  saveLand(land: Land, event: Event): void {
    event.stopPropagation();
    this.landService.update(land).subscribe({
      next: () => { this.editingLandId = null; this.snapshot = null; this.loadLands(); }
    });
  }

  calculate(land: Land): void {
    land.totalCost     = (land.lengthInSqft || 0) * (land.widthInSqft || 0) * (land.purchaseRatePerSqft || 0);
    land.balanceAmount = land.totalCost - (land.paidAmount || 0);
  }
}