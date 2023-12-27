import { IJobTitle, NewJobTitle } from './job-title.model';

export const sampleWithRequiredData: IJobTitle = {
  id: 18757,
  titleAr: 'reassemble wherever',
  titleLat: 'rigidly roust save',
};

export const sampleWithPartialData: IJobTitle = {
  id: 10573,
  titleAr: 'zowie ragged',
  titleLat: 'yahoo asparagus',
};

export const sampleWithFullData: IJobTitle = {
  id: 25070,
  titleAr: 'woot clank underground',
  titleLat: 'er repulsive supersede',
  description: 'pfft geez',
};

export const sampleWithNewData: NewJobTitle = {
  titleAr: 'absent alongside courageously',
  titleLat: 'crick eek villainous',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
