import dayjs from 'dayjs/esm';
import { IFollowUp } from 'app/entities/follow-up/follow-up.model';
import { ICotery } from 'app/entities/cotery/cotery.model';
import { IStudent } from 'app/entities/student/student.model';
import { Attendance } from 'app/entities/enumerations/attendance.model';

export interface ICoteryHistory {
  id: number;
  date?: dayjs.Dayjs | null;
  coteryName?: string | null;
  studentFullName?: string | null;
  attendanceStatus?: keyof typeof Attendance | null;
  followUp?: IFollowUp | null;
  student2?: ICotery | null;
  student?: IStudent | null;
}

export type NewCoteryHistory = Omit<ICoteryHistory, 'id'> & { id: null };
