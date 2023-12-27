import { IReview, NewReview } from './review.model';

export const sampleWithRequiredData: IReview = {
  id: 13063,
  body: 'horseradish',
};

export const sampleWithPartialData: IReview = {
  id: 3416,
  body: 'out lashes pfft',
  rating: 4,
};

export const sampleWithFullData: IReview = {
  id: 2495,
  body: 'fiercely which documentation',
  rating: 5,
};

export const sampleWithNewData: NewReview = {
  body: 'equatorial',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
