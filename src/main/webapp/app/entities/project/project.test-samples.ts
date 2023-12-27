import dayjs from 'dayjs/esm';

import { IProject, NewProject } from './project.model';

export const sampleWithRequiredData: IProject = {
  id: 2783,
  titleAr: 'than proliferate',
  titleLat: 'whoever short sausage',
  budget: 23889.49,
};

export const sampleWithPartialData: IProject = {
  id: 30273,
  titleAr: 'amidst',
  titleLat: 'dark',
  description: 'size',
  budget: 4609.69,
  startDate: dayjs('2023-12-25'),
};

export const sampleWithFullData: IProject = {
  id: 20555,
  titleAr: 'toothpaste',
  titleLat: 'slippery',
  description: 'tenderly remorseful',
  goals: 'thoughtfully option merrily',
  requirement: 'sense',
  imageLink: '../fake-data/blob/hipster.png',
  imageLinkContentType: 'unknown',
  videoLink: 'recover underneath zowie',
  budget: 9918.11,
  isActive: false,
  activateAt: dayjs('2023-12-24'),
  startDate: dayjs('2023-12-25'),
  endDate: dayjs('2023-12-24'),
};

export const sampleWithNewData: NewProject = {
  titleAr: 'examiner er often',
  titleLat: 'disassociate round',
  budget: 8060.88,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
