import dayjs from 'dayjs/esm';
import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { ICourse } from 'app/entities/course/course.model';
import { ISession } from 'app/entities/session/session.model';

export interface IProfessor {
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
  courses?: ICourse[] | null;
  sessions?: ISession[] | null;
}

export type NewProfessor = Omit<IProfessor, 'id'> & { id: null };
