import { ILanguage, NewLanguage } from './language.model';

export const sampleWithRequiredData: ILanguage = {
  id: 31281,
  label: 'fox for sauce',
};

export const sampleWithPartialData: ILanguage = {
  id: 14052,
  label: 'aw past',
};

export const sampleWithFullData: ILanguage = {
  id: 1281,
  label: 'correct virtuous',
};

export const sampleWithNewData: NewLanguage = {
  label: 'unlike cloak awesome',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
