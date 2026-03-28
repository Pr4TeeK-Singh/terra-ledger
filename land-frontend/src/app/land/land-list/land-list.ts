import {
  Component, OnInit, OnDestroy, ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';

import { AgGridAngular } from 'ag-grid-angular';
import {
  ColDef, GridApi, GridReadyEvent, CellValueChangedEvent,
  RowSelectedEvent, ModuleRegistry, AllCommunityModule,
  themeQuartz
} from 'ag-grid-community';

ModuleRegistry.registerModules([AllCommunityModule]);

import { Land } from '../../models/land';
import { LandService } from '../../services/land.service';
import { PlotPanelComponent } from '../plot-panel/plot-panel';

@Component({
  selector: 'app-land-list',
  standalone: true,
  imports: [CommonModule, AgGridAngular, PlotPanelComponent],
  templateUrl: './land-list.html',
  styleUrl: './land-list.css'
})
export class LandListComponent implements OnInit, OnDestroy {

  lands: Land[] = [];
  selectedLand: Land | null = null;
  private gridApi!: GridApi;
  private sub!: Subscription;

  readonly theme = themeQuartz.withParams({
  backgroundColor:          '#111118',
  foregroundColor:          '#f0f0f5',
  borderColor:              'rgba(255,255,255,0.06)',
  chromeBackgroundColor:    '#18181f',
  headerBackgroundColor:    '#18181f',
  headerTextColor:          '#9898a8',
  headerFontWeight:         600,
  rowHoverColor:            '#1f1f28',
  selectedRowBackgroundColor: 'rgba(0,230,118,0.08)',
  oddRowBackgroundColor:    '#111118',
  inputBorder:              '1px solid rgba(255,255,255,0.10)',
  inputBackgroundColor:     '#0a0a0f',
  inputTextColor:           '#f0f0f5',
  inputFocusBorder:         '1px solid #00e676',
  inputFocusShadow:         '0 0 0 2px rgba(0,230,118,0.12)',
  fontFamily:               "'Inter', sans-serif",
  fontSize:                 13,
  cellTextColor:            '#f0f0f5',
  rangeSelectionBorderColor:'#00e676',
  checkboxCheckedBackgroundColor: '#00e676',
  checkboxCheckedBorderColor:     '#00e676',
  checkboxUncheckedBorderColor:   'rgba(255,255,255,0.20)',
  panelBackgroundColor:'#18181f',
  columnBorder:             { style: 'none' },
  wrapperBorder:            { style: 'none' },
  rowBorder:                { color: 'rgba(255,255,255,0.04)', style: 'solid', width: 1 },
  headerColumnResizeHandleColor: 'rgba(255,255,255,0.15)',
  headerColumnResizeHandleWidth: 2,
});

  columnDefs: ColDef[] = [
    {
      field: 'gaataNumber',
      headerName: 'Gaata No.',
      editable: true,
      flex: 1,
      minWidth: 120,
      filter: 'agTextColumnFilter'
    },
    {
      field: 'locationAddress',
      headerName: 'Location',
      editable: true,
      flex: 2,
      minWidth: 180,
      filter: 'agTextColumnFilter'
    },
    {
      field: 'owner.name',
      headerName: 'Owner',
      editable: false,
      flex: 1,
      minWidth: 130,
      filter: 'agTextColumnFilter',
      valueGetter: p => p.data?.owner?.name ?? '—'
    },
    {
      field: 'lengthInSqft',
      headerName: 'Length (sqft)',
      editable: true,
      flex: 1,
      minWidth: 120,
      filter: 'agNumberColumnFilter',
      valueParser: p => Number(p.newValue)
    },
    {
      field: 'widthInSqft',
      headerName: 'Width (sqft)',
      editable: true,
      flex: 1,
      minWidth: 120,
      filter: 'agNumberColumnFilter',
      valueParser: p => Number(p.newValue)
    },
    {
      field: 'purchaseRatePerSqft',
      headerName: 'Rate/sqft (₹)',
      editable: true,
      flex: 1,
      minWidth: 130,
      filter: 'agNumberColumnFilter',
      valueParser: p => Number(p.newValue),
      valueFormatter: p => p.value ? `₹${Number(p.value).toLocaleString('en-IN')}` : '—'
    },
    {
      field: 'totalCost',
      headerName: 'Total Cost (₹)',
      editable: false,
      flex: 1,
      minWidth: 140,
      filter: 'agNumberColumnFilter',
      valueFormatter: p => p.value ? `₹${Number(p.value).toLocaleString('en-IN')}` : '—',
      cellStyle: { fontWeight: '600', color: '#1e5c3a' }
    },
    {
      field: 'paidAmount',
      headerName: 'Paid (₹)',
      editable: true,
      flex: 1,
      minWidth: 120,
      filter: 'agNumberColumnFilter',
      valueParser: p => Number(p.newValue),
      valueFormatter: p => p.value ? `₹${Number(p.value).toLocaleString('en-IN')}` : '—'
    },
    {
      field: 'balanceAmount',
      headerName: 'Balance (₹)',
      editable: false,
      flex: 1,
      minWidth: 130,
      filter: 'agNumberColumnFilter',
      valueFormatter: p => p.value ? `₹${Number(p.value).toLocaleString('en-IN')}` : '—',
      cellStyle: p => ({
        fontWeight: '600',
        color: Number(p.value) > 0 ? '#b91c1c' : '#1e5c3a'
      })
    },
    {
      field: 'contractStartDate',
      headerName: 'Start Date',
      editable: true,
      flex: 1,
      minWidth: 130,
      filter: 'agDateColumnFilter'
    },
    {
      field: 'contractEndDate',
      headerName: 'End Date',
      editable: true,
      flex: 1,
      minWidth: 130,
      filter: 'agDateColumnFilter'
    }
  ];

  defaultColDef: ColDef = {
    sortable: true,
    resizable: true,
    suppressHeaderMenuButton: false
  };

  pagination = true;
  paginationPageSize = 10;
  paginationPageSizeSelector = [5, 10, 20, 50];

  constructor(
    private landService: LandService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.sub = this.landService.refresh$.subscribe(() => this.loadLands());
    this.loadLands();
  }

  ngOnDestroy(): void { this.sub?.unsubscribe(); }

  loadLands(): void {
    this.landService.getAllLands().subscribe({
      next: lands => {
        this.lands = lands;
        this.cdr.markForCheck();
      }
    });
  }

  onGridReady(event: GridReadyEvent): void {
    this.gridApi = event.api;
  }

  onRowSelected(event: RowSelectedEvent): void {
    const selected = this.gridApi.getSelectedRows();
    this.selectedLand = selected.length > 0 ? selected[0] : null;
  }

  onCellValueChanged(event: CellValueChangedEvent): void {
    const land: Land = { ...event.data };

    // Recalculate totals when dimensions/rate/paid change
    const recalcFields = ['lengthInSqft', 'widthInSqft', 'purchaseRatePerSqft', 'paidAmount'];
    if (recalcFields.includes(event.colDef.field!)) {
      land.totalCost = (land.lengthInSqft || 0) * (land.widthInSqft || 0) * (land.purchaseRatePerSqft || 0);
      land.balanceAmount = land.totalCost - (land.paidAmount || 0);
    }

    this.landService.update(land).subscribe({
      next: () => this.loadLands()
    });
  }

  deleteSelected(): void {
    const selected = this.gridApi.getSelectedRows();
    if (!selected.length) return;
    // Wire up a DELETE endpoint when ready — for now shows confirmation
    if (confirm(`Delete ${selected.length} land record(s)?`)) {
      // TODO: call landService.delete(id) when backend endpoint is added
      alert('Delete endpoint not yet implemented on the backend.');
    }
  }

  exportCsv(): void {
    this.gridApi.exportDataAsCsv({ fileName: 'land-records.csv' });
  }

  clearFilters(): void {
    this.gridApi.setFilterModel(null);
  }
}