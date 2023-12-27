import dayjs from 'dayjs/esm';
import { ICoteryHistory } from 'app/entities/cotery-history/cotery-history.model';
import { ICertificate } from 'app/entities/certificate/certificate.model';
import { Attendance } from 'app/entities/enumerations/attendance.model';

export interface ICotery {
  id: number;
  date?: dayjs.Dayjs | null;
  coteryName?: string | null;
  studentFullName?: string | null;
  attendanceStatus?: keyof typeof Attendance | null;
  coteryHistories?: ICoteryHistory[] | null;
  certificates?: ICertificate[] | null;
}

export type NewCotery = Omit<ICotery, 'id'> & { id: null };
