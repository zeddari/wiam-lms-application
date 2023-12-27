import dayjs from 'dayjs/esm';

import { IEnrolement, NewEnrolement } from './enrolement.model';

export const sampleWithRequiredData: IEnrolement = {
  id: 2493,
  isActive: true,
  enrolmentStartTime: dayjs('2023-12-25T10:16'),
  enrolemntEndTime: dayjs('2023-12-25T10:19'),
};

export const sampleWithPartialData: IEnrolement = {
  id: 17796,
  isActive: true,
  enrolmentStartTime: dayjs('2023-12-24T23:26'),
  enrolemntEndTime: dayjs('2023-12-25T10:25'),
};

export const sampleWithFullData: IEnrolement = {
  id: 3649,
  isActive: false,
  activatedAt: dayjs('2023-12-24T19:28'),
  activatedBy: dayjs('2023-12-25T05:26'),
  enrolmentStartTime: dayjs('2023-12-24T13:05'),
  enrolemntEndTime: dayjs('2023-12-24T22:29'),
};

export const sampleWithNewData: NewEnrolement = {
  isActive: true,
  enrolmentStartTime: dayjs('2023-12-24T23:42'),
  enrolemntEndTime: dayjs('2023-12-24T20:50'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
