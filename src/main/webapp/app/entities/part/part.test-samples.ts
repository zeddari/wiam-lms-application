import { IPart, NewPart } from './part.model';

export const sampleWithRequiredData: IPart = {
  id: 26860,
  titleAr: 'boohoo',
  titleLat: 'pain except',
};

export const sampleWithPartialData: IPart = {
  id: 24553,
  titleAr: 'incidentally furthermore',
  titleLat: 'thaw',
  imageLink: '../fake-data/blob/hipster.png',
  imageLinkContentType: 'unknown',
  videoLink: 'yahoo probe drafty',
};

export const sampleWithFullData: IPart = {
  id: 29362,
  titleAr: 'whoever incidentally across',
  titleLat: 'suggest',
  description: 'gender',
  duration: 27834,
  imageLink: '../fake-data/blob/hipster.png',
  imageLinkContentType: 'unknown',
  videoLink: 'even',
};

export const sampleWithNewData: NewPart = {
  titleAr: 'baby pfft accent',
  titleLat: 'instead given skateboard',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
