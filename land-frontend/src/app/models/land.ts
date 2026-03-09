import { Owner } from './owner';

export interface Land {
  landId?: number;
  gaataNumber: string;
  locationAddress: string;
  lengthInSqft: number;
  widthInSqft: number;
  purchaseRatePerSqft: number;
  totalCost: number;
  paidAmount: number;
  balanceAmount: number;
  contractStartDate: string;
  contractEndDate: string;
  ownerId?: number;
  owner?: Owner | null;
}