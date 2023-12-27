import dayjs from 'dayjs/esm';

import { ICourse, NewCourse } from './course.model';

export const sampleWithRequiredData: ICourse = {
  id: 13815,
  titleAr: 'between amid',
  titleLat: 'remind',
};

export const sampleWithPartialData: ICourse = {
  id: 4617,
  titleAr: 'shatter',
  titleLat: 'from monsoon even',
  subTitles: 'where euphoric while',
  price: 7109.49,
  isActive: false,
};

export const sampleWithFullData: ICourse = {
  id: 31086,
  titleAr: 'incomparable an unaccountably',
  titleLat: 'fat plumb keenly',
  description: 'backyard',
  subTitles: 'outside',
  requirement: 'excepting leaf kookily',
  duration: 16809,
  option: 'silently circa oof',
  type: true,
  imageLink: '../fake-data/blob/hipster.png',
  imageLinkContentType: 'unknown',
  videoLink: 'apropos',
  price: 28705.27,
  isActive: false,
  activateAt: dayjs('2023-12-25'),
  isConfirmed: false,
  confirmedAt: dayjs('2023-12-25'),
};

export const sampleWithNewData: NewCourse = {
  titleAr: 'farm separately',
  titleLat: 'righteously parachute',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
