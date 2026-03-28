import {
  Component, Input, OnChanges, SimpleChanges
} from '@angular/core';
import { CommonModule } from '@angular/common';

import { AgGridAngular } from 'ag-grid-angular';
import {
  ColDef, GridApi, GridReadyEvent, CellValueChangedEvent,
  RowSelectedEvent, ModuleRegistry, AllCommunityModule,
  themeQuartz
} from 'ag-grid-community';

ModuleRegistry.registerModules([AllCommunityModule]);

import { Land } from '../../models/land';
import { Plot } from '../../models/plot';
import { PlotService } from '../../services/plot.service';
import { PlotFormComponent } from '../../plot-form/plot-form';



@Component({
  selector: 'app-plot-panel',
  standalone: true,
  imports: [CommonModule, AgGridAngular, PlotFormComponent],
  templateUrl: './plot-panel.html',
  styleUrl: './plot-panel.css'
})
export class PlotPanelComponent implements OnChanges {

  @Input() land!: Land;

  plots: Plot[] = [];
  selectedPlot: Plot | null = null;
  showAddForm = false;
  private gridApi!: GridApi;

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
  panelBackgroundColor: '#18181f',
  columnBorder:             { style: 'none' },
  wrapperBorder:            { style: 'none' },
  rowBorder:                { color: 'rgba(255,255,255,0.04)', style: 'solid', width: 1 },
  headerColumnResizeHandleColor: 'rgba(255,255,255,0.15)',
  headerColumnResizeHandleWidth: 2,
});

  columnDefs: ColDef[] = [
    {
      field: 'plotNo',
      headerName: 'Plot No.',
      editable: true,
      flex: 1,
      minWidth: 100,
      filter: 'agTextColumnFilter'
    },
    {
      field: 'gaataNo',
      headerName: 'Gaata No.',
      editable: true,
      flex: 1,
      minWidth: 110,
      filter: 'agTextColumnFilter'
    },
    {
      field: 'landLength',
      headerName: 'Length (sqft)',
      editable: true,
      flex: 1,
      minWidth: 120,
      filter: 'agNumberColumnFilter',
      valueParser: p => Number(p.newValue)
    },
    {
      field: 'landWidth',
      headerName: 'Width (sqft)',
      editable: true,
      flex: 1,
      minWidth: 110,
      filter: 'agNumberColumnFilter',
      valueParser: p => Number(p.newValue)
    },
    {
      field: 'sellRate',
      headerName: 'Sell Rate (₹)',
      editable: true,
      flex: 1,
      minWidth: 120,
      filter: 'agNumberColumnFilter',
      valueParser: p => Number(p.newValue),
      valueFormatter: p => p.value ? `₹${Number(p.value).toLocaleString('en-IN')}` : '—'
    },
    {
      field: 'totalAmount',
      headerName: 'Total (₹)',
      editable: false,
      flex: 1,
      minWidth: 120,
      valueFormatter: p => p.value ? `₹${Number(p.value).toLocaleString('en-IN')}` : '—',
      cellStyle: { fontWeight: '600', color: '#1e5c3a' }
    },
    {
      field: 'status',
      headerName: 'Status',
      editable: true,
      flex: 1,
      minWidth: 120,
      filter: 'agTextColumnFilter',
      cellEditor: 'agSelectCellEditor',
      cellEditorParams: { values: ['AVAILABLE', 'SOLD', 'RESERVED'] },
      cellStyle: p => {
        const colors: Record<string, string> = {
          AVAILABLE: '#1e5c3a',
          SOLD: '#b91c1c',
          RESERVED: '#92400e'
        };
        return { fontWeight: '600', color: colors[p.value] ?? '#6B6560' };
      }
    },
    {
      field: 'seller.name',
      headerName: 'Seller',
      editable: false,
      flex: 1,
      minWidth: 120,
      filter: 'agTextColumnFilter',
      valueGetter: p => p.data?.seller?.name ?? '—'
    },
    {
      field: 'broker.name',
      headerName: 'Broker',
      editable: false,
      flex: 1,
      minWidth: 120,
      filter: 'agTextColumnFilter',
      valueGetter: p => p.data?.broker?.name ?? '—'
    }
  ];

  defaultColDef: ColDef = {
    sortable: true,
    resizable: true
  };

  constructor(private plotService: PlotService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['land'] && this.land?.landId) {
      this.showAddForm = false;
      this.selectedPlot = null;
      this.plots = [];                    // ✅ clear immediately so grid shows empty before data loads

      // ✅ clear any active filters when switching lands
      if (this.gridApi) {
        this.gridApi.setFilterModel(null);
        this.gridApi.deselectAll();
      }

      this.loadPlots();
    }
  }

  loadPlots(): void {
    this.plotService.getByLandId(this.land.landId!).subscribe({
      next: plots => {
        this.plots = [...plots];          // ✅ spread forces new array reference — AG Grid detects the change
        if (this.gridApi) {
          this.gridApi.setGridOption('rowData', this.plots);  // ✅ explicitly push new data to grid
        }
      }
    });
  }

  onGridReady(event: GridReadyEvent): void {
    this.gridApi = event.api;
    // ✅ load plots once grid is ready in case land was already set
    if (this.land?.landId) {
      this.loadPlots();
    }
  }

  onRowSelected(event: RowSelectedEvent): void {
    const selected = this.gridApi.getSelectedRows();
    this.selectedPlot = selected.length > 0 ? selected[0] : null;
  }

  onCellValueChanged(event: CellValueChangedEvent): void {
    const plot: Plot = { ...event.data };

    const recalcFields = ['landLength', 'landWidth', 'sellRate'];
    if (recalcFields.includes(event.colDef.field!)) {
      plot.totalAmount = (plot.landLength || 0) * (plot.landWidth || 0) * (plot.sellRate || 0);
    }

    this.plotService.update(plot).subscribe({
      next: () => this.loadPlots()
    });
  }

  onPlotSaved(): void {
    this.loadPlots();
    this.showAddForm = false;
  }

  exportCsv(): void {
    this.gridApi?.exportDataAsCsv({ fileName: `plots-land-${this.land.landId}.csv` });
  }

  clearFilters(): void {
    this.gridApi?.setFilterModel(null);
  }
}