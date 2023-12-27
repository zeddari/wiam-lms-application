import { IFollowUp, NewFollowUp } from './follow-up.model';

export const sampleWithRequiredData: IFollowUp = {
  id: 5705,
};

export const sampleWithPartialData: IFollowUp = {
  id: 30706,
  fromSourate: 'BA9ARA',
  remarks: 'smuggle peak',
};

export const sampleWithFullData: IFollowUp = {
  id: 32234,
  fromSourate: 'FATIHA',
  fromAya: 21714,
  toSourate: 'BA9ARA',
  toAya: 20432,
  tilawaType: 'HIFD',
  notation: 'coliseum why',
  remarks: 'gregarious incidentally amalgamate',
};

export const sampleWithNewData: NewFollowUp = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
