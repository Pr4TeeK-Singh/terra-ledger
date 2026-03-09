import { Component, OnInit, Output, EventEmitter, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Land } from '../../models/land';
import { Owner } from '../../models/owner';
import { LandService } from '../../services/land.service';

@Component({
  selector: 'app-land-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './land-form.html',
  styleUrl: './land-form.css'
})
export class LandFormComponent implements OnInit {

  @Output() landSaved = new EventEmitter<void>();

  land: Land = this.createEmptyLand();
  owners: Owner[] = [];
  showOwnerModal = false;
  newOwner: Owner = this.emptyOwner();

  constructor(private landService: LandService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void { this.loadOwnersFromLands(); }

  loadOwnersFromLands(): void {
    this.landService.getAllLands().subscribe({
      next: lands => {
        const map = new Map<number, Owner>();
        lands.forEach(l => { if (l.owner?.ownerId) map.set(l.owner.ownerId, l.owner); });
        this.owners = Array.from(map.values());
        this.cdr.markForCheck();
      }
    });
  }

  calculate(): void {
    this.land.totalCost     = (this.land.lengthInSqft || 0) * (this.land.widthInSqft || 0) * (this.land.purchaseRatePerSqft || 0);
    this.land.balanceAmount = this.land.totalCost - (this.land.paidAmount || 0);
  }

  onOwnerSelect(id: number): void { if (id) this.land.owner = null; }

  getSelectedOwnerName(): string {
    return this.owners.find(o => o.ownerId === this.land.ownerId)?.name || '';
  }

  openOwnerModal(): void  { this.showOwnerModal = true; }
  closeOwnerModal(): void { this.showOwnerModal = false; this.newOwner = this.emptyOwner(); }

  attachNewOwner(): void {
    this.land.ownerId = undefined;
    this.land.owner   = { ...this.newOwner };
    this.closeOwnerModal();
  }

  save(): void {
    this.landService.save(this.land).subscribe({
      next: () => {
        this.land = this.createEmptyLand();
        this.loadOwnersFromLands();
        this.landService.triggerRefresh();
        this.landSaved.emit();
      }
    });
  }

  createEmptyLand(): Land {
    return {
      gaataNumber: '', locationAddress: '',
      lengthInSqft: 0, widthInSqft: 0, purchaseRatePerSqft: 0,
      totalCost: 0, paidAmount: 0, balanceAmount: 0,
      contractStartDate: '', contractEndDate: '',
      ownerId: undefined, owner: null
    };
  }

  emptyOwner(): Owner { return { name: '', contactNo: '', address: '', aadharNo: '' }; }
}