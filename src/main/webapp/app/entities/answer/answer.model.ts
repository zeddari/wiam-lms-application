import { IQuestion } from 'app/entities/question/question.model';
import { IStudent } from 'app/entities/student/student.model';

export interface IAnswer {
  id: number;
  a1v?: boolean | null;
  a2v?: boolean | null;
  a3v?: boolean | null;
  a4v?: boolean | null;
  result?: boolean | null;
  question?: IQuestion | null;
  student?: IStudent | null;
}

export type NewAnswer = Omit<IAnswer, 'id'> & { id: null };
