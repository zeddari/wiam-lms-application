import dayjs from 'dayjs/esm';

import { IProfessor, NewProfessor } from './professor.model';

export const sampleWithRequiredData: IProfessor = {
  id: 14715,
};

export const sampleWithPartialData: IProfessor = {
  id: 11174,
  mobileNumber: 'meanwhile',
  gender: true,
  about: 'educated',
  imageLink: '../fake-data/blob/hipster.png',
  imageLinkContentType: 'unknown',
  birthdate: dayjs('2023-12-25'),
  lastDegree: 'unethically',
};

export const sampleWithFullData: IProfessor = {
  id: 25761,
  phoneNumber: 'gah arrogantly',
  mobileNumber: 'under autosave',
  gender: false,
  about: 'wherever',
  imageLink: '../fake-data/blob/hipster.png',
  imageLinkContentType: 'unknown',
  code: 'yippee',
  birthdate: dayjs('2023-12-25'),
  lastDegree: 'toy acclaim sari',
};

export const sampleWithNewData: NewProfessor = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
