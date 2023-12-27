import { IExam, NewExam } from './exam.model';

export const sampleWithRequiredData: IExam = {
  id: 29428,
  tajweedScore: 5444,
  hifdScore: 31621,
  adaeScore: 8206,
  decision: 5404,
};

export const sampleWithPartialData: IExam = {
  id: 9710,
  studentName: 'gregarious anchored enormously',
  examName: 'er',
  tajweedScore: 6556,
  hifdScore: 8380,
  adaeScore: 5711,
  observation: '../fake-data/blob/hipster.txt',
  decision: 11225,
};

export const sampleWithFullData: IExam = {
  id: 30994,
  comite: 'hopelessly trailer on',
  studentName: 'gosh instead',
  examName: 'toast private',
  examCategory: 'NARRATION_OF_HAFS_ON_THE_AUTHORITY_OF_AL_KASAI_VIA_AL_SHATIBIYYAH',
  examType: 'NEW_HIFD',
  tajweedScore: 19486,
  hifdScore: 3739,
  adaeScore: 15214,
  observation: '../fake-data/blob/hipster.txt',
  decision: 26152,
};

export const sampleWithNewData: NewExam = {
  tajweedScore: 19972,
  hifdScore: 13447,
  adaeScore: 32047,
  decision: 31429,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
