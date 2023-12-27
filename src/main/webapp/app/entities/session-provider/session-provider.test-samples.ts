import { ISessionProvider, NewSessionProvider } from './session-provider.model';

export const sampleWithRequiredData: ISessionProvider = {
  id: 30394,
  name: 'along glisten piccolo',
};

export const sampleWithPartialData: ISessionProvider = {
  id: 20942,
  name: 'complicated oof below',
  description: 'about meh',
};

export const sampleWithFullData: ISessionProvider = {
  id: 19182,
  name: 'dirty',
  description: 'uselessly showy',
  website: 'hungrily welcome since',
};

export const sampleWithNewData: NewSessionProvider = {
  name: 'apud',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
