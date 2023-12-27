import { IGroup, NewGroup } from './group.model';

export const sampleWithRequiredData: IGroup = {
  id: 32074,
  nameAr: 'off so continually',
  nameLat: 'new even however',
};

export const sampleWithPartialData: IGroup = {
  id: 3801,
  nameAr: 'before where maker',
  nameLat: 'lance videotape',
  description: 'poverty up',
};

export const sampleWithFullData: IGroup = {
  id: 635,
  nameAr: 'renege tame',
  nameLat: 'rarely phew',
  description: 'loud replacement',
};

export const sampleWithNewData: NewGroup = {
  nameAr: 'grounded plague opposite',
  nameLat: 'while',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
