import { ISite } from 'app/entities/site/site.model';

export interface ICity {
  id: number;
  nameAr?: string | null;
  nameLat?: string | null;
  sites?: ISite[] | null;
}

export type NewCity = Omit<ICity, 'id'> & { id: null };
