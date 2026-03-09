import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Land } from '../models/land';

@Injectable({ providedIn: 'root' })
export class LandService {

  private readonly baseUrl = 'http://localhost:8080/api/lands';
  private refreshSubject = new Subject<void>();
  refresh$ = this.refreshSubject.asObservable();

  constructor(private http: HttpClient) {}

  getAllLands(): Observable<Land[]> {
    return this.http.get<Land[]>(this.baseUrl);
  }

  save(land: Land): Observable<any> {
    return this.http.post(this.baseUrl, land);
  }

  update(land: Land): Observable<any> {
    return this.http.put(`${this.baseUrl}/${land.landId}`, land);
  }

  triggerRefresh(): void {
    this.refreshSubject.next();
  }
}