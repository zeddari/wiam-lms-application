import { ICurrency, NewCurrency } from './currency.model';

export const sampleWithRequiredData: ICurrency = {
  id: 25547,
  nameAr: 'tightly',
  nameLat: 'ah curtain bony',
  code: 'across rou',
};

export const sampleWithPartialData: ICurrency = {
  id: 28920,
  nameAr: 'including including about',
  nameLat: 'litmus publisher',
  code: 'compliance',
};

export const sampleWithFullData: ICurrency = {
  id: 9439,
  nameAr: 'interest endeavour',
  nameLat: 'than',
  code: 'considerin',
};

export const sampleWithNewData: NewCurrency = {
  nameAr: 'pish estimate',
  nameLat: 'meh toward',
  code: 'elope duri',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
