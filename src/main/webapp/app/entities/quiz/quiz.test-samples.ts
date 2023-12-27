import { IQuiz, NewQuiz } from './quiz.model';

export const sampleWithRequiredData: IQuiz = {
  id: 12532,
};

export const sampleWithPartialData: IQuiz = {
  id: 11986,
  quizType: 'QUIZ_TYPE3',
  quizDescription: 'alongside',
};

export const sampleWithFullData: IQuiz = {
  id: 14759,
  quizTitle: 'nor commonly',
  quizType: 'QUIZ_TYPE2',
  quizDescription: 'gracefully stock whoever',
};

export const sampleWithNewData: NewQuiz = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
