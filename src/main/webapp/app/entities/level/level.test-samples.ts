import { ILevel, NewLevel } from './level.model';

export const sampleWithRequiredData: ILevel = {
  id: 7008,
  titleAr: 'barren under',
  titleLat: 'grout duh before',
};

export const sampleWithPartialData: ILevel = {
  id: 5707,
  titleAr: 'minus finally boost',
  titleLat: 'gadzooks',
};

export const sampleWithFullData: ILevel = {
  id: 8980,
  titleAr: 'silly',
  titleLat: 'index',
};

export const sampleWithNewData: NewLevel = {
  titleAr: 'over',
  titleLat: 'appear cultivated pr',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
