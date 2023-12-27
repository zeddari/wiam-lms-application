import { IQuestion2, NewQuestion2 } from './question-2.model';

export const sampleWithRequiredData: IQuestion2 = {
  id: 16215,
};

export const sampleWithPartialData: IQuestion2 = {
  id: 4356,
  questionTitle: 'prime',
  questionDescription: 'shrill',
  questionSubject: 'fervently tingle',
};

export const sampleWithFullData: IQuestion2 = {
  id: 24862,
  questionTitle: 'generally',
  questionType: 'QUES_TYPE3',
  questionDescription: 'individuate but dump',
  questionPoint: 20693,
  questionSubject: 'doubtfully',
  questionStatus: 'left lap',
};

export const sampleWithNewData: NewQuestion2 = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
