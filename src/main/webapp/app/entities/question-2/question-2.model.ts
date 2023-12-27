import { QuestionType } from 'app/entities/enumerations/question-type.model';

export interface IQuestion2 {
  id: number;
  questionTitle?: string | null;
  questionType?: keyof typeof QuestionType | null;
  questionDescription?: string | null;
  questionPoint?: number | null;
  questionSubject?: string | null;
  questionStatus?: string | null;
}

export type NewQuestion2 = Omit<IQuestion2, 'id'> & { id: null };
