import { IProgressionMode, NewProgressionMode } from './progression-mode.model';

export const sampleWithRequiredData: IProgressionMode = {
  id: 24971,
  titleAr: 'sweetly what',
  titleLat: 'pish',
};

export const sampleWithPartialData: IProgressionMode = {
  id: 9407,
  titleAr: 'end',
  titleLat: 'dazzle',
};

export const sampleWithFullData: IProgressionMode = {
  id: 27336,
  titleAr: 'almost experienced i',
  titleLat: 'odd near',
};

export const sampleWithNewData: NewProgressionMode = {
  titleAr: 'phooey',
  titleLat: 'handball per',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
