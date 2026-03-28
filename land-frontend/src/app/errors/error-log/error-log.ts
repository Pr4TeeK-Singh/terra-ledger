import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ErrorLogService } from '../../services/error-log.service';
import { AppError } from '../../models/app-error';

@Component({
  selector: 'app-error-log',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-log.html',
  styleUrl: './error-log.css'
})
export class ErrorLogComponent implements OnInit {

  errors: AppError[] = [];

  constructor(
    public errorLog: ErrorLogService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.errorLog.errors$.subscribe(e => this.errors = e);
  }

  clear(): void { this.errorLog.clear(); }
  back(): void  { this.router.navigate(['/']); }

  badgeClass(type: string): string {
    return type === 'HTTP' ? 'badge-http' : type === 'RUNTIME' ? 'badge-runtime' : 'badge-unknown';
  }

  statusClass(status?: number): string {
    if (!status) return '';
    if (status >= 500) return 'status-red';
    if (status >= 400) return 'status-amber';
    return 'status-green';
  }
}