import { ICountry2, NewCountry2 } from './country-2.model';

export const sampleWithRequiredData: ICountry2 = {
  id: 32592,
  countryName: 'eek gah',
};

export const sampleWithPartialData: ICountry2 = {
  id: 6628,
  countryName: 'excluding whoa with',
};

export const sampleWithFullData: ICountry2 = {
  id: 1462,
  countryName: 'eminent wooden',
};

export const sampleWithNewData: NewCountry2 = {
  countryName: 'which',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
