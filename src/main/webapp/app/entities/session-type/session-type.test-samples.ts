import { ISessionType, NewSessionType } from './session-type.model';

export const sampleWithRequiredData: ISessionType = {
  id: 27802,
  title: 'uh-huh',
};

export const sampleWithPartialData: ISessionType = {
  id: 18554,
  title: 'badly uh-huh sculpt',
};

export const sampleWithFullData: ISessionType = {
  id: 6181,
  title: 'usable knowledgeably lead',
  description: 'because yahoo before',
};

export const sampleWithNewData: NewSessionType = {
  title: 'on lovingly befriend',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
