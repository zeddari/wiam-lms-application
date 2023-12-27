import dayjs from 'dayjs/esm';
import { IPayment } from 'app/entities/payment/payment.model';
import { IStudent } from 'app/entities/student/student.model';
import { ICourse } from 'app/entities/course/course.model';

export interface IEnrolement {
  id: number;
  isActive?: boolean | null;
  activatedAt?: dayjs.Dayjs | null;
  activatedBy?: dayjs.Dayjs | null;
  enrolmentStartTime?: dayjs.Dayjs | null;
  enrolemntEndTime?: dayjs.Dayjs | null;
  payments?: IPayment[] | null;
  student?: IStudent | null;
  course?: ICourse | null;
}

export type NewEnrolement = Omit<IEnrolement, 'id'> & { id: null };
