import dayjs from 'dayjs/esm';
import { ILanguage } from 'app/entities/language/language.model';
import { ICountry } from 'app/entities/country/country.model';
import { IJob } from 'app/entities/job/job.model';
import { IExam } from 'app/entities/exam/exam.model';
import { ISponsor } from 'app/entities/sponsor/sponsor.model';
import { IEmployee } from 'app/entities/employee/employee.model';
import { IProfessor } from 'app/entities/professor/professor.model';
import { IStudent } from 'app/entities/student/student.model';
import { Role } from 'app/entities/enumerations/role.model';
import { AccountStatus } from 'app/entities/enumerations/account-status.model';
import { Sex } from 'app/entities/enumerations/sex.model';

export interface IUserCustom {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  accountName?: string | null;
  role?: keyof typeof Role | null;
  status?: keyof typeof AccountStatus | null;
  password?: string | null;
  phoneNumber1?: string | null;
  phoneNumver2?: string | null;
  sex?: keyof typeof Sex | null;
  countryInternalId?: number | null;
  nationalityId?: number | null;
  birthDay?: dayjs.Dayjs | null;
  photo?: string | null;
  photoContentType?: string | null;
  address?: string | null;
  facebook?: string | null;
  telegramUserCustomId?: string | null;
  telegramUserCustomName?: string | null;
  biography?: string | null;
  bankAccountDetails?: string | null;
  certificate?: string | null;
  certificateContentType?: string | null;
  jobInternalId?: number | null;
  creationDate?: dayjs.Dayjs | null;
  modificationDate?: dayjs.Dayjs | null;
  deletionDate?: dayjs.Dayjs | null;
  languages?: ILanguage[] | null;
  country?: ICountry | null;
  job?: IJob | null;
  exams?: IExam[] | null;
  sponsor?: ISponsor | null;
  employee?: IEmployee | null;
  professor?: IProfessor | null;
  student?: IStudent | null;
}

export type NewUserCustom = Omit<IUserCustom, 'id'> & { id: null };