import { ICountry, NewCountry } from './country.model';

export const sampleWithRequiredData: ICountry = {
  id: 11272,
  nameAr: 'wonderfully',
  nameLat: 'outside joyfully sequestrate',
};

export const sampleWithPartialData: ICountry = {
  id: 101,
  nameAr: 'role circa',
  nameLat: 'costly than transfix',
};

export const sampleWithFullData: ICountry = {
  id: 20392,
  nameAr: 'sizzling',
  nameLat: 'acclaimed legislate',
  code: 'freely eek',
};

export const sampleWithNewData: NewCountry = {
  nameAr: 'grin',
  nameLat: 'modulo',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
