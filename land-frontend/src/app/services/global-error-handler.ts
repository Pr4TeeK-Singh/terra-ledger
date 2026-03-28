import { ErrorHandler, Injectable, inject } from '@angular/core';
import { ErrorLogService } from './error-log.service';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {

  private errorLog = inject(ErrorLogService);

  handleError(error: unknown): void {
    const message = error instanceof Error
      ? error.message
      : String(error);

    this.errorLog.add({ type: 'RUNTIME', message });
    console.error('[GlobalErrorHandler]', error);
  }
}