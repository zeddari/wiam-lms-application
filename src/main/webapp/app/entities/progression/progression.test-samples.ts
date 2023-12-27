import dayjs from 'dayjs/esm';

import { IProgression, NewProgression } from './progression.model';

export const sampleWithRequiredData: IProgression = {
  id: 25360,
  status: true,
  taskDone: false,
  progressionDate: dayjs('2023-12-24'),
};

export const sampleWithPartialData: IProgression = {
  id: 18150,
  status: false,
  justifRef: 'cop-out schuss while',
  lateArrival: 26959,
  earlyDeparture: 26178,
  taskDone: false,
  grade1: 'lest',
  description: 'hoof amused',
  taskStart: 181,
  taskEnd: 273,
  taskStep: 27067,
  progressionDate: dayjs('2023-12-24'),
};

export const sampleWithFullData: IProgression = {
  id: 26266,
  status: false,
  isJustified: false,
  justifRef: 'pfft',
  lateArrival: 4198,
  earlyDeparture: 23017,
  taskDone: false,
  grade1: 'wherever mid',
  description: 'of',
  taskStart: 272,
  taskEnd: 305,
  taskStep: 18500,
  progressionDate: dayjs('2023-12-25'),
};

export const sampleWithNewData: NewProgression = {
  status: true,
  taskDone: false,
  progressionDate: dayjs('2023-12-25'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
