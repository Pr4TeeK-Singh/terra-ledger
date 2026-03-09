import { Seller } from './seller';
import { Broker } from './broker';


export interface Plot {
  plotId?: number;
  gaataNo: string;
  landId: number;
  plotNo: string;
  landLength: number;
  landWidth: number;
  sellRate: number;
  totalAmount: number;
  status: 'AVAILABLE' | 'SOLD' | 'RESERVED';
  sellerId?: number;
  seller?: Seller | null;
  brokerId?: number;
  broker?: Broker | null;
}