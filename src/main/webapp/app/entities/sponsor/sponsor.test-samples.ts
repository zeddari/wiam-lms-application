import dayjs from 'dayjs/esm';

import { ISponsor, NewSponsor } from './sponsor.model';

export const sampleWithRequiredData: ISponsor = {
  id: 16284,
};

export const sampleWithPartialData: ISponsor = {
  id: 11306,
  phoneNumber: 'aw',
  birthdate: dayjs('2023-12-24'),
  lastDegree: 'equally toothpick across',
};

export const sampleWithFullData: ISponsor = {
  id: 20034,
  phoneNumber: 'when',
  mobileNumber: 'angina',
  gender: false,
  about: 'meanwhile quirkily',
  imageLink: '../fake-data/blob/hipster.png',
  imageLinkContentType: 'unknown',
  code: 'trusty gather',
  birthdate: dayjs('2023-12-25'),
  lastDegree: 'worn unnaturally',
};

export const sampleWithNewData: NewSponsor = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
