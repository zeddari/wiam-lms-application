import dayjs from 'dayjs/esm';

import { IUserCustom, NewUserCustom } from './user-custom.model';

export const sampleWithRequiredData: IUserCustom = {
  id: 16836,
  firstName: 'Tyrese',
  lastName: 'Ziemann',
  email: 'Chris13@hotmail.com',
  accountName: 'Checking Account',
  role: 'SUPER_MANAGER',
  status: 'ACTIVATED',
  password: 'given inside',
  phoneNumber1: 'light kindheartedly',
  sex: 'MALE',
  countryInternalId: 14625,
  nationalityId: 8982,
  birthDay: dayjs('2023-12-25'),
  creationDate: dayjs('2023-12-25T03:48'),
};

export const sampleWithPartialData: IUserCustom = {
  id: 25019,
  firstName: 'Jayden',
  lastName: 'Schuppe',
  email: 'Elinore_Gleason@gmail.com',
  accountName: 'Home Loan Account',
  role: 'STUDENT',
  status: 'BLOCKED',
  password: 'component reverse',
  phoneNumber1: 'what',
  phoneNumver2: 'yowza deceivingly',
  sex: 'FEMALE',
  countryInternalId: 31673,
  nationalityId: 2786,
  birthDay: dayjs('2023-12-24'),
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  facebook: 'lest before suppression',
  telegramUserCustomName: 'menacing forte',
  biography: 'scared',
  bankAccountDetails: 'yippee',
  creationDate: dayjs('2023-12-25T08:34'),
  modificationDate: dayjs('2023-12-24T17:15'),
  deletionDate: dayjs('2023-12-24T23:11'),
};

export const sampleWithFullData: IUserCustom = {
  id: 8983,
  firstName: 'Keegan',
  lastName: 'Fisher',
  email: 'Sasha73@hotmail.com',
  accountName: 'Money Market Account',
  role: 'STUDENT',
  status: 'DEACTIVATED',
  password: 'instead amidst happily',
  phoneNumber1: 'whoever',
  phoneNumver2: 'shoat',
  sex: 'MALE',
  countryInternalId: 21100,
  nationalityId: 27772,
  birthDay: dayjs('2023-12-25'),
  photo: '../fake-data/blob/hipster.png',
  photoContentType: 'unknown',
  address: 'likewise',
  facebook: 'aboard that',
  telegramUserCustomId: 'gosh sadly',
  telegramUserCustomName: 'preach',
  biography: 'overbalance',
  bankAccountDetails: 'when',
  certificate: '../fake-data/blob/hipster.png',
  certificateContentType: 'unknown',
  jobInternalId: 12346,
  creationDate: dayjs('2023-12-24T19:00'),
  modificationDate: dayjs('2023-12-24T14:42'),
  deletionDate: dayjs('2023-12-24T19:20'),
};

export const sampleWithNewData: NewUserCustom = {
  firstName: 'Kory',
  lastName: 'Leuschke',
  email: 'Kristopher_Bechtelar59@yahoo.com',
  accountName: 'Savings Account',
  role: 'MANAGEMENT',
  status: 'DEACTIVATED',
  password: 'zowie con phooey',
  phoneNumber1: 'rigidly golden',
  sex: 'FEMALE',
  countryInternalId: 58,
  nationalityId: 25396,
  birthDay: dayjs('2023-12-24'),
  creationDate: dayjs('2023-12-24T13:33'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
