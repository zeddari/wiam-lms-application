import { IQuestion, NewQuestion } from './question.model';

export const sampleWithRequiredData: IQuestion = {
  id: 16586,
  question: 'spatter',
  a1: 'if',
  a1v: false,
  a2: 'striking',
  a2v: true,
  isactive: true,
};

export const sampleWithPartialData: IQuestion = {
  id: 26951,
  question: 'whoever ha whenever',
  a1: 'absent',
  a1v: false,
  a2: 'between weekender the',
  a2v: true,
  a3v: true,
  a4v: true,
  isactive: true,
  questionTitle: 'lie oof lest',
  questionPoint: 11559,
  questionSubject: 'by improbable',
};

export const sampleWithFullData: IQuestion = {
  id: 18191,
  question: 'righteously',
  note: 'mediocre',
  a1: 'oh whether',
  a1v: true,
  a2: 'almost',
  a2v: true,
  a3: 'spin athwart microwave',
  a3v: true,
  a4: 'redefine loudly shakily',
  a4v: false,
  isactive: true,
  questionTitle: 'agreeable',
  questionType: 'QUES_TYPE3',
  questionDescription: 'near drat definite',
  questionPoint: 29472,
  questionSubject: 'for unlike before',
  questionStatus: 'blindly happily',
};

export const sampleWithNewData: NewQuestion = {
  question: 'atop',
  a1: 'serialize misty',
  a1v: false,
  a2: 'if subtle',
  a2v: false,
  isactive: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
