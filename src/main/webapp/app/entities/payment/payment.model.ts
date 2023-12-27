import dayjs from 'dayjs/esm';
import { ICurrency } from 'app/entities/currency/currency.model';
import { IEnrolement } from 'app/entities/enrolement/enrolement.model';
import { PaymentType } from 'app/entities/enumerations/payment-type.model';

export interface IPayment {
  id: number;
  paymentMethod?: string | null;
  paiedBy?: string | null;
  mode?: string | null;
  poof?: string | null;
  poofContentType?: string | null;
  paidAt?: dayjs.Dayjs | null;
  amount?: string | null;
  type?: keyof typeof PaymentType | null;
  fromMonth?: number | null;
  toMonth?: number | null;
  details?: string | null;
  currency?: ICurrency | null;
  enrolment?: IEnrolement | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
