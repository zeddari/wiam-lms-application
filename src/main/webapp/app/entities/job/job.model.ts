import dayjs from 'dayjs/esm';
import { IUserCustom } from 'app/entities/user-custom/user-custom.model';

export interface IJob {
  id: number;
  title?: string | null;
  description?: string | null;
  creationDate?: dayjs.Dayjs | null;
  manager?: number | null;
  userCustoms?: IUserCustom[] | null;
}

export type NewJob = Omit<IJob, 'id'> & { id: null };
