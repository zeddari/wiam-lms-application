import { IDiplomaType, NewDiplomaType } from './diploma-type.model';

export const sampleWithRequiredData: IDiplomaType = {
  id: 2451,
  titleAr: 'psst',
};

export const sampleWithPartialData: IDiplomaType = {
  id: 16773,
  titleAr: 'amid spleen fava',
  titleLat: 'mild afore eek',
};

export const sampleWithFullData: IDiplomaType = {
  id: 6457,
  titleAr: 'horrible shunt',
  titleLat: 'client excepting duh',
};

export const sampleWithNewData: NewDiplomaType = {
  titleAr: 'supposing',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
