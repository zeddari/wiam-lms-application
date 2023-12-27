import dayjs from 'dayjs/esm';

import { ICotery, NewCotery } from './cotery.model';

export const sampleWithRequiredData: ICotery = {
  id: 2978,
};

export const sampleWithPartialData: ICotery = {
  id: 389,
  date: dayjs('2023-12-24'),
  coteryName: 'until brave given',
  studentFullName: 'remote',
  attendanceStatus: 'ABSENT',
};

export const sampleWithFullData: ICotery = {
  id: 16131,
  date: dayjs('2023-12-25'),
  coteryName: 'resound altitude meanwhile',
  studentFullName: 'positively',
  attendanceStatus: 'ABSENT',
};

export const sampleWithNewData: NewCotery = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
