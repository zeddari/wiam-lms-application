import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { Riwayats } from 'app/entities/enumerations/riwayats.model';
import { ExamType } from 'app/entities/enumerations/exam-type.model';

export interface IExam {
  id: number;
  comite?: string | null;
  studentName?: string | null;
  examName?: string | null;
  examCategory?: keyof typeof Riwayats | null;
  examType?: keyof typeof ExamType | null;
  tajweedScore?: number | null;
  hifdScore?: number | null;
  adaeScore?: number | null;
  observation?: string | null;
  decision?: number | null;
  userCustoms?: IUserCustom[] | null;
}

export type NewExam = Omit<IExam, 'id'> & { id: null };
