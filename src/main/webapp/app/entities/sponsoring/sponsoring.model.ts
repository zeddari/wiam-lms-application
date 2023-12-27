import dayjs from 'dayjs/esm';
import { ISponsor } from 'app/entities/sponsor/sponsor.model';
import { IProject } from 'app/entities/project/project.model';
import { ICurrency } from 'app/entities/currency/currency.model';

export interface ISponsoring {
  id: number;
  message?: string | null;
  amount?: number | null;
  startDate?: dayjs.Dayjs | null;
  endDate?: dayjs.Dayjs | null;
  isAlways?: boolean | null;
  sponsor?: ISponsor | null;
  project?: IProject | null;
  currency?: ICurrency | null;
}

export type NewSponsoring = Omit<ISponsoring, 'id'> & { id: null };
