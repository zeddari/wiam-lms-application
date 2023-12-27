import { IAnswer } from 'app/entities/answer/answer.model';
import { ICourse } from 'app/entities/course/course.model';
import { IQuiz } from 'app/entities/quiz/quiz.model';
import { IQuizCertificate } from 'app/entities/quiz-certificate/quiz-certificate.model';
import { QuestionType } from 'app/entities/enumerations/question-type.model';

export interface IQuestion {
  id: number;
  question?: string | null;
  note?: string | null;
  a1?: string | null;
  a1v?: boolean | null;
  a2?: string | null;
  a2v?: boolean | null;
  a3?: string | null;
  a3v?: boolean | null;
  a4?: string | null;
  a4v?: boolean | null;
  isactive?: boolean | null;
  questionTitle?: string | null;
  questionType?: keyof typeof QuestionType | null;
  questionDescription?: string | null;
  questionPoint?: number | null;
  questionSubject?: string | null;
  questionStatus?: string | null;
  answers?: IAnswer[] | null;
  course?: ICourse | null;
  quizzes?: IQuiz[] | null;
  quizCertificates?: IQuizCertificate[] | null;
}

export type NewQuestion = Omit<IQuestion, 'id'> & { id: null };
