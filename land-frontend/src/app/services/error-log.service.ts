import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppError } from '../models/app-error';

@Injectable({ providedIn: 'root' })
export class ErrorLogService {

  private errors: AppError[] = [];
  private errorsSubject = new BehaviorSubject<AppError[]>([]);
  errors$ = this.errorsSubject.asObservable();

  add(error: Omit<AppError, 'id' | 'timestamp'>): void {
    const entry: AppError = {
      ...error,
      id:        crypto.randomUUID(),
      timestamp: new Date()
    };
    this.errors = [entry, ...this.errors].slice(0, 100); // keep last 100
    this.errorsSubject.next(this.errors);
    console.error(`[ErrorLog] ${entry.type} | ${entry.message}`, entry);
  }

  clear(): void {
    this.errors = [];
    this.errorsSubject.next([]);
  }

  get count(): number { return this.errors.length; }
}