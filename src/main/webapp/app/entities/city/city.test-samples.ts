import { ICity, NewCity } from './city.model';

export const sampleWithRequiredData: ICity = {
  id: 31083,
  nameAr: 'evenly fend',
  nameLat: 'unless meaningfully fanny-pack',
};

export const sampleWithPartialData: ICity = {
  id: 11543,
  nameAr: 'duh',
  nameLat: 'development under',
};

export const sampleWithFullData: ICity = {
  id: 9986,
  nameAr: 'for lot',
  nameLat: 'who',
};

export const sampleWithNewData: NewCity = {
  nameAr: 'indeed',
  nameLat: 'irk deficient',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
