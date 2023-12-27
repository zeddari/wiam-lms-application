import dayjs from 'dayjs/esm';

import { IEmployee, NewEmployee } from './employee.model';

export const sampleWithRequiredData: IEmployee = {
  id: 16086,
};

export const sampleWithPartialData: IEmployee = {
  id: 31679,
  mobileNumber: 'or goggle',
  gender: true,
  about: 'meanwhile',
  birthdate: dayjs('2023-12-24'),
};

export const sampleWithFullData: IEmployee = {
  id: 26954,
  phoneNumber: 'allay often blah',
  mobileNumber: 'ah',
  gender: true,
  about: 'suspiciously lest stimulus',
  imageLink: '../fake-data/blob/hipster.png',
  imageLinkContentType: 'unknown',
  code: 'than',
  birthdate: dayjs('2023-12-25'),
  lastDegree: 'helpful salty pub',
};

export const sampleWithNewData: NewEmployee = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
