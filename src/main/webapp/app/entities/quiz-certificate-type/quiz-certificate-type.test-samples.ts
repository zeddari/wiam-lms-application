import { IQuizCertificateType, NewQuizCertificateType } from './quiz-certificate-type.model';

export const sampleWithRequiredData: IQuizCertificateType = {
  id: 10227,
  titleAr: 'beneath',
};

export const sampleWithPartialData: IQuizCertificateType = {
  id: 17490,
  titleAr: 'idealistic campaigni',
  titleLat: 'whoa dwell',
};

export const sampleWithFullData: IQuizCertificateType = {
  id: 2329,
  titleAr: 'hmph moralise',
  titleLat: 'phooey oof narrow',
};

export const sampleWithNewData: NewQuizCertificateType = {
  titleAr: 'stamp knowledgeable',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
