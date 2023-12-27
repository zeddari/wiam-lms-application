import { IStudent } from 'app/entities/student/student.model';
import { IUserCustom } from 'app/entities/user-custom/user-custom.model';

export interface ICountry {
  id: number;
  nameAr?: string | null;
  nameLat?: string | null;
  code?: string | null;
  students?: IStudent[] | null;
  userCustoms?: IUserCustom[] | null;
}

export type NewCountry = Omit<ICountry, 'id'> & { id: null };
