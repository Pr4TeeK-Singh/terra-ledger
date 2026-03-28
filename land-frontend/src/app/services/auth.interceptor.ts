import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { ErrorLogService } from './error-log.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token    = inject(AuthService).getToken();
  const errorLog = inject(ErrorLogService);

  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      const message = err.error?.message || err.error?.error || err.message || 'Unknown error';
      errorLog.add({
        type:    'HTTP',
        message: message,
        url:     req.url,
        status:  err.status
      });
      return throwError(() => err);
    })
  );
};