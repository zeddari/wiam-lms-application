import { ISessionLink, NewSessionLink } from './session-link.model';

export const sampleWithRequiredData: ISessionLink = {
  id: 32721,
  title: 'trot pish intuit',
};

export const sampleWithPartialData: ISessionLink = {
  id: 22092,
  title: 'why disbelieve bash',
  description: 'diligently offensively',
  link: 'hmph',
};

export const sampleWithFullData: ISessionLink = {
  id: 27115,
  title: 'lung excited',
  description: 'a',
  link: 'where train eminent',
};

export const sampleWithNewData: NewSessionLink = {
  title: 'pleasant steepen',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
