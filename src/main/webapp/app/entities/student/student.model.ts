import dayjs from 'dayjs/esm';
import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { ICoteryHistory } from 'app/entities/cotery-history/cotery-history.model';
import { ICertificate } from 'app/entities/certificate/certificate.model';
import { IEnrolement } from 'app/entities/enrolement/enrolement.model';
import { IAnswer } from 'app/entities/answer/answer.model';
import { IProgression } from 'app/entities/progression/progression.model';
import { IGroup } from 'app/entities/group/group.model';
import { ICountry } from 'app/entities/country/country.model';
import { ISponsor } from 'app/entities/sponsor/sponsor.model';
import { IQuizCertificate } from 'app/entities/quiz-certificate/quiz-certificate.model';

export interface IStudent {
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
  coteryHistories?: ICoteryHistory[] | null;
  certificates?: ICertificate[] | null;
  enrolements?: IEnrolement[] | null;
  answers?: IAnswer[] | null;
  progressions?: IProgression[] | null;
  group2?: IGroup | null;
  country?: ICountry | null;
  sponsors?: ISponsor[] | null;
  quizCertificates?: IQuizCertificate[] | null;
}

export type NewStudent = Omit<IStudent, 'id'> & { id: null };
