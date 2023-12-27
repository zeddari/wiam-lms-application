import { IClassroom, NewClassroom } from './classroom.model';

export const sampleWithRequiredData: IClassroom = {
  id: 25387,
  nameAr: 'gratefully hunger',
  nameLat: 'whenever',
};

export const sampleWithPartialData: IClassroom = {
  id: 28476,
  nameAr: 'legal',
  nameLat: 'future throughout',
  description: 'yowza',
};

export const sampleWithFullData: IClassroom = {
  id: 30102,
  nameAr: 'mmm provided incidentally',
  nameLat: 'now',
  description: 'but judgementally whenever',
};

export const sampleWithNewData: NewClassroom = {
  nameAr: 'piece jell',
  nameLat: 'innocently',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
