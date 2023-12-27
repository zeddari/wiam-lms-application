import { ISponsoring } from 'app/entities/sponsoring/sponsoring.model';
import { IPayment } from 'app/entities/payment/payment.model';

export interface ICurrency {
  id: number;
  nameAr?: string | null;
  nameLat?: string | null;
  code?: string | null;
  sponsorings?: ISponsoring[] | null;
  payments?: IPayment[] | null;
}

export type NewCurrency = Omit<ICurrency, 'id'> & { id: null };
