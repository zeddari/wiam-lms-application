import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 3701,
  paymentMethod: 'ken badly',
  paiedBy: 'meanwhile',
  mode: 'properly canvass hmph',
  paidAt: dayjs('2023-12-25T04:31'),
  type: 'ACTIVITY_FEES',
  fromMonth: 17639,
  toMonth: 7995,
};

export const sampleWithPartialData: IPayment = {
  id: 22808,
  paymentMethod: 'mortally soulful',
  paiedBy: 'ouch',
  mode: 'lest',
  paidAt: dayjs('2023-12-24T16:13'),
  type: 'REGISTER',
  fromMonth: 182,
  toMonth: 6225,
};

export const sampleWithFullData: IPayment = {
  id: 12522,
  paymentMethod: 'too ah',
  paiedBy: 'amongst because',
  mode: 'yum hm which',
  poof: '../fake-data/blob/hipster.png',
  poofContentType: 'unknown',
  paidAt: dayjs('2023-12-25T09:16'),
  amount: 'participant elderly gleefully',
  type: 'ACTIVITY_FEES',
  fromMonth: 7095,
  toMonth: 1506,
  details: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewPayment = {
  paymentMethod: 'per degrease mechanise',
  paiedBy: 'ew',
  mode: 'psst vain enraged',
  paidAt: dayjs('2023-12-25T10:13'),
  type: 'ACTIVITY_FEES',
  fromMonth: 6831,
  toMonth: 25101,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
