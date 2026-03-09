import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Plot } from '../models/plot';

@Injectable({ providedIn: 'root' })
export class PlotService {

  private readonly baseUrl = 'http://localhost:8080/api/plots';
  private refreshSubject = new Subject<void>();
  refresh$ = this.refreshSubject.asObservable();

  constructor(private http: HttpClient) {}

  getByLandId(landId: number): Observable<Plot[]> {
    return this.http.get<Plot[]>(`${this.baseUrl}/land/${landId}`);
  }

  save(plot: Plot): Observable<any> {
    return this.http.post(this.baseUrl, plot);
  }

  update(plot: Plot): Observable<any> {
    return this.http.put(`${this.baseUrl}/${plot.plotId}`, plot);
  }

  triggerRefresh(): void {
    this.refreshSubject.next();
  }
}