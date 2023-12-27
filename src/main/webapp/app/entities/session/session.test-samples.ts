import dayjs from 'dayjs/esm';

import { ISession, NewSession } from './session.model';

export const sampleWithRequiredData: ISession = {
  id: 5366,
  title: 'prudent',
  sessionStartTime: dayjs('2023-12-25T06:56'),
  sessionEndTime: dayjs('2023-12-24T14:57'),
  isActive: false,
  sessionSize: 7,
  currency: 'than seldom',
  targetedAge: 'miniature overconfidently',
  targetedGender: false,
  planningType: 'redefine',
};

export const sampleWithPartialData: ISession = {
  id: 29926,
  title: 'dimly round',
  description: 'yummy shoulder layout',
  sessionStartTime: dayjs('2023-12-25T01:41'),
  sessionEndTime: dayjs('2023-12-25T02:06'),
  isActive: false,
  sessionSize: 14,
  price: 4435.95,
  currency: 'silently insignificant moral',
  targetedAge: 'pity a long',
  targetedGender: true,
  planningType: 'flagellate for',
  wednesday: false,
  friday: true,
  saturday: true,
  periodStartDate: dayjs('2023-12-25'),
  noPeriodeEndDate: true,
};

export const sampleWithFullData: ISession = {
  id: 23735,
  title: 'sheepishly nor',
  description: 'where eek',
  sessionStartTime: dayjs('2023-12-25T10:18'),
  sessionEndTime: dayjs('2023-12-24T16:55'),
  isActive: false,
  sessionSize: 8,
  price: 25056.52,
  currency: 'upon supposing yippee',
  targetedAge: 'sadly',
  targetedGender: true,
  thumbnail: '../fake-data/blob/hipster.png',
  thumbnailContentType: 'unknown',
  planningType: 'extremely neonate ball',
  onceDate: dayjs('2023-12-24T13:24'),
  monday: false,
  tuesday: false,
  wednesday: false,
  thursday: true,
  friday: false,
  saturday: true,
  sanday: false,
  periodStartDate: dayjs('2023-12-24'),
  periodeEndDate: dayjs('2023-12-24'),
  noPeriodeEndDate: false,
};

export const sampleWithNewData: NewSession = {
  title: 'brother-in-law',
  sessionStartTime: dayjs('2023-12-25T06:53'),
  sessionEndTime: dayjs('2023-12-24T21:18'),
  isActive: false,
  sessionSize: 38,
  currency: 'sandpaper softly',
  targetedAge: 'skylight arrive',
  targetedGender: false,
  planningType: 'uniform thoroughly',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
