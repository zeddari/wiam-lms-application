import { ISessionJoinMode, NewSessionJoinMode } from './session-join-mode.model';

export const sampleWithRequiredData: ISessionJoinMode = {
  id: 21063,
  title: 'scary hmph belated',
};

export const sampleWithPartialData: ISessionJoinMode = {
  id: 5588,
  title: 'whoever',
  description: 'optimistically',
};

export const sampleWithFullData: ISessionJoinMode = {
  id: 21496,
  title: 'falsify bitterly',
  description: 'beyond or',
};

export const sampleWithNewData: NewSessionJoinMode = {
  title: 'mmm furthermore',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
