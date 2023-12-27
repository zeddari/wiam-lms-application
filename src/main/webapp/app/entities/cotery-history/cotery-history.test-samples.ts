import dayjs from 'dayjs/esm';

import { ICoteryHistory, NewCoteryHistory } from './cotery-history.model';

export const sampleWithRequiredData: ICoteryHistory = {
  id: 32205,
};

export const sampleWithPartialData: ICoteryHistory = {
  id: 12049,
  date: dayjs('2023-12-24'),
  coteryName: 'excepting park',
};

export const sampleWithFullData: ICoteryHistory = {
  id: 7286,
  date: dayjs('2023-12-25'),
  coteryName: 'underpay ah rightfully',
  studentFullName: 'finally whereas indeed',
  attendanceStatus: 'ABSENT',
};

export const sampleWithNewData: NewCoteryHistory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
