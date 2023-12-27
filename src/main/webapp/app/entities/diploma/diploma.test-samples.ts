import dayjs from 'dayjs/esm';

import { IDiploma, NewDiploma } from './diploma.model';

export const sampleWithRequiredData: IDiploma = {
  id: 24987,
  title: 'content beside',
};

export const sampleWithPartialData: IDiploma = {
  id: 21359,
  title: 'since bear',
  subject: 'earmark pale finally',
  detail: 'reassuringly ha',
  supervisor: 'pepper among supposing',
  graduationDate: dayjs('2023-12-24'),
  school: 'although ferociously boldly',
};

export const sampleWithFullData: IDiploma = {
  id: 12648,
  title: 'lace',
  subject: 'solitaire yowza',
  detail: 'ouch',
  supervisor: 'a',
  grade: 'but',
  graduationDate: dayjs('2023-12-25'),
  school: 'bush',
};

export const sampleWithNewData: NewDiploma = {
  title: 'phew',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
