import { ICertificate, NewCertificate } from './certificate.model';

export const sampleWithRequiredData: ICertificate = {
  id: 11984,
};

export const sampleWithPartialData: ICertificate = {
  id: 4555,
  coteryName: 'psst',
};

export const sampleWithFullData: ICertificate = {
  id: 8963,
  coteryName: 'than',
  studentFullName: 'yuck carelessly at',
  certificateType: 'TAJWID',
};

export const sampleWithNewData: NewCertificate = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
