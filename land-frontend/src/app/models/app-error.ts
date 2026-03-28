export interface AppError {
  id:        string;
  type:      'HTTP' | 'RUNTIME' | 'UNKNOWN';
  message:   string;
  url?:      string;
  status?:   number;
  timestamp: Date;
}