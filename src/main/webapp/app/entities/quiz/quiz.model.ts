import { IQuestion } from 'app/entities/question/question.model';
import { QuizType } from 'app/entities/enumerations/quiz-type.model';

export interface IQuiz {
  id: number;
  quizTitle?: string | null;
  quizType?: keyof typeof QuizType | null;
  quizDescription?: string | null;
  questions?: IQuestion[] | null;
}

export type NewQuiz = Omit<IQuiz, 'id'> & { id: null };
