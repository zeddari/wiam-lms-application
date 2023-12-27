import { ISite, NewSite } from './site.model';

export const sampleWithRequiredData: ISite = {
  id: 7368,
  nameAr: 'overconfidently glom likely',
  nameLat: 'across excepting',
};

export const sampleWithPartialData: ISite = {
  id: 27589,
  nameAr: 'ha trouble surprisingly',
  nameLat: 'as afore',
  description: 'beside headbutt but',
  localisation: 'cloth',
};

export const sampleWithFullData: ISite = {
  id: 1506,
  nameAr: 'however appendix',
  nameLat: 'boo hamper exactly',
  description: 'arrest',
  localisation: 'interchange',
};

export const sampleWithNewData: NewSite = {
  nameAr: 'who likewise',
  nameLat: 'enchanting',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
