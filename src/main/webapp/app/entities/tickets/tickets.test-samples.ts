import dayjs from 'dayjs/esm';

import { ITickets, NewTickets } from './tickets.model';

export const sampleWithRequiredData: ITickets = {
  id: 2615,
  title: 'staff shackle since',
  dateTicket: dayjs('2023-12-24T22:13'),
};

export const sampleWithPartialData: ITickets = {
  id: 18512,
  title: 'meanwhile blah',
  description: 'finally phew yowza',
  justifDoc: '../fake-data/blob/hipster.png',
  justifDocContentType: 'unknown',
  dateTicket: dayjs('2023-12-24T22:33'),
  dateProcess: dayjs('2023-12-25T00:01'),
  processed: false,
};

export const sampleWithFullData: ITickets = {
  id: 31781,
  title: 'briskly',
  description: 'below',
  justifDoc: '../fake-data/blob/hipster.png',
  justifDocContentType: 'unknown',
  dateTicket: dayjs('2023-12-25T07:23'),
  dateProcess: dayjs('2023-12-24T15:09'),
  processed: true,
};

export const sampleWithNewData: NewTickets = {
  title: 'bike consequently bossy',
  dateTicket: dayjs('2023-12-25T01:44'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
