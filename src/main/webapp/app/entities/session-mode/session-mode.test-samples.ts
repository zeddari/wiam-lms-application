import { ISessionMode, NewSessionMode } from './session-mode.model';

export const sampleWithRequiredData: ISessionMode = {
  id: 10379,
  title: 'oof',
};

export const sampleWithPartialData: ISessionMode = {
  id: 16646,
  title: 'chain unimportant cloakroom',
  description: 'so overstay',
};

export const sampleWithFullData: ISessionMode = {
  id: 7986,
  title: 'following',
  description: 'atop hm',
};

export const sampleWithNewData: NewSessionMode = {
  title: 'following thirsty thankfully',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
