import { IDepartement, NewDepartement } from './departement.model';

export const sampleWithRequiredData: IDepartement = {
  id: 5252,
  nameAr: 'concerning',
  nameLat: 'suspiciously',
};

export const sampleWithPartialData: IDepartement = {
  id: 26429,
  nameAr: 'underneath illiterate',
  nameLat: 'considering',
  description: 'roughly into',
};

export const sampleWithFullData: IDepartement = {
  id: 21787,
  nameAr: 'rout catalyse',
  nameLat: 'hill ugh sardonic',
  description: 'inexperienced fluid dutiful',
};

export const sampleWithNewData: NewDepartement = {
  nameAr: 'defect process defoliate',
  nameLat: 'before ignorant',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
