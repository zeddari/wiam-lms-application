import dayjs from 'dayjs/esm';
import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { IDepartement } from 'app/entities/departement/departement.model';
import { IJobTitle } from 'app/entities/job-title/job-title.model';
import { ISession } from 'app/entities/session/session.model';

export interface IEmployee {
  id: number;
  phoneNumber?: string | null;
  mobileNumber?: string | null;
  gender?: boolean | null;
  about?: string | null;
  imageLink?: string | null;
  imageLinkContentType?: string | null;
  code?: string | null;
  birthdate?: dayjs.Dayjs | null;
  lastDegree?: string | null;
  userCustom?: IUserCustom | null;
  departement?: IDepartement | null;
  job?: IJobTitle | null;
  sessions?: ISession[] | null;
}

export type NewEmployee = Omit<IEmployee, 'id'> & { id: null };
