import { IQuizCertificate, NewQuizCertificate } from './quiz-certificate.model';

export const sampleWithRequiredData: IQuizCertificate = {
  id: 11031,
  title: 'out barring incidentally',
  description: 'brazen er ha',
  isActive: false,
};

export const sampleWithPartialData: IQuizCertificate = {
  id: 13053,
  title: 'suspiciously',
  description: 'perfectly before',
  isActive: true,
};

export const sampleWithFullData: IQuizCertificate = {
  id: 4763,
  title: 'selfishly tuba',
  description: 'declaration',
  isActive: true,
};

export const sampleWithNewData: NewQuizCertificate = {
  title: 'blissfully without',
  description: 'terribly',
  isActive: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
