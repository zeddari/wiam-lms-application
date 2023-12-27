import dayjs from 'dayjs/esm';
import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { ISponsoring } from 'app/entities/sponsoring/sponsoring.model';
import { IStudent } from 'app/entities/student/student.model';

export interface ISponsor {
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
  sponsorings?: ISponsoring[] | null;
  students?: IStudent[] | null;
}

export type NewSponsor = Omit<ISponsor, 'id'> & { id: null };
