import dayjs from 'dayjs/esm';

import { ISponsoring, NewSponsoring } from './sponsoring.model';

export const sampleWithRequiredData: ISponsoring = {
  id: 16273,
  amount: 7012.8,
};

export const sampleWithPartialData: ISponsoring = {
  id: 8899,
  message: 'apropos whose',
  amount: 12814.6,
  endDate: dayjs('2023-12-24'),
  isAlways: true,
};

export const sampleWithFullData: ISponsoring = {
  id: 26696,
  message: 'judgementally exactly blog',
  amount: 24707.72,
  startDate: dayjs('2023-12-25'),
  endDate: dayjs('2023-12-25'),
  isAlways: true,
};

export const sampleWithNewData: NewSponsoring = {
  amount: 29317.47,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
