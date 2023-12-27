import { IAnswer, NewAnswer } from './answer.model';

export const sampleWithRequiredData: IAnswer = {
  id: 22613,
  a1v: true,
  a2v: true,
  result: false,
};

export const sampleWithPartialData: IAnswer = {
  id: 22808,
  a1v: true,
  a2v: true,
  a4v: false,
  result: true,
};

export const sampleWithFullData: IAnswer = {
  id: 27706,
  a1v: true,
  a2v: true,
  a3v: false,
  a4v: true,
  result: true,
};

export const sampleWithNewData: NewAnswer = {
  a1v: true,
  a2v: true,
  result: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
