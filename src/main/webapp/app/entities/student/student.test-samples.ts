import dayjs from 'dayjs/esm';

import { IStudent, NewStudent } from './student.model';

export const sampleWithRequiredData: IStudent = {
  id: 23105,
};

export const sampleWithPartialData: IStudent = {
  id: 24217,
  phoneNumber: 'yippee',
  mobileNumber: 'because afore',
  gender: true,
  code: 'judicious',
  birthdate: dayjs('2023-12-25'),
};

export const sampleWithFullData: IStudent = {
  id: 11041,
  phoneNumber: 'deny darling',
  mobileNumber: 'eclipse',
  gender: false,
  about: 'in ah',
  imageLink: '../fake-data/blob/hipster.png',
  imageLinkContentType: 'unknown',
  code: 'minnow lollipop',
  birthdate: dayjs('2023-12-24'),
  lastDegree: 'or sadly',
};

export const sampleWithNewData: NewStudent = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
