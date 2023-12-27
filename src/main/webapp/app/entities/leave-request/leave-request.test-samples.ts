import dayjs from 'dayjs/esm';

import { ILeaveRequest, NewLeaveRequest } from './leave-request.model';

export const sampleWithRequiredData: ILeaveRequest = {
  id: 16910,
  from: dayjs('2023-12-25T04:59'),
  toDate: dayjs('2023-12-25T00:22'),
};

export const sampleWithPartialData: ILeaveRequest = {
  id: 8599,
  from: dayjs('2023-12-25T04:02'),
  toDate: dayjs('2023-12-24T19:15'),
  details: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: ILeaveRequest = {
  id: 23269,
  title: 'reluctance harmless',
  from: dayjs('2023-12-25T03:08'),
  toDate: dayjs('2023-12-25T03:36'),
  details: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewLeaveRequest = {
  from: dayjs('2023-12-24T19:35'),
  toDate: dayjs('2023-12-24T20:57'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
