import dayjs from 'dayjs/esm';

import { IJob, NewJob } from './job.model';

export const sampleWithRequiredData: IJob = {
  id: 13958,
  title: 'instead',
  creationDate: dayjs('2023-12-25T03:40'),
};

export const sampleWithPartialData: IJob = {
  id: 7634,
  title: 'absent progression industry',
  creationDate: dayjs('2023-12-24T17:28'),
  manager: 2920,
};

export const sampleWithFullData: IJob = {
  id: 23570,
  title: 'aw lost',
  description: 'pepper',
  creationDate: dayjs('2023-12-24T22:20'),
  manager: 24418,
};

export const sampleWithNewData: NewJob = {
  title: 'perfectly blurt a',
  creationDate: dayjs('2023-12-25T09:25'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
