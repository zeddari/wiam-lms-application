import { ITopic, NewTopic } from './topic.model';

export const sampleWithRequiredData: ITopic = {
  id: 21905,
  titleAr: 'autoimmunity sock',
  titleLat: 'unripe nearest behest',
};

export const sampleWithPartialData: ITopic = {
  id: 28778,
  titleAr: 'ew rubric',
  titleLat: 'artichoke',
};

export const sampleWithFullData: ITopic = {
  id: 5930,
  titleAr: 'plumber senate nectar',
  titleLat: 'lumpy satisfy',
  description: 'instead tidy',
};

export const sampleWithNewData: NewTopic = {
  titleAr: 'constant geez jolly',
  titleLat: 'psst nor',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
