import { ITicketSubjects, NewTicketSubjects } from './ticket-subjects.model';

export const sampleWithRequiredData: ITicketSubjects = {
  id: 19565,
  title: 'avaricious',
};

export const sampleWithPartialData: ITicketSubjects = {
  id: 13238,
  title: 'gah arrest',
  description: 'until inside boohoo',
};

export const sampleWithFullData: ITicketSubjects = {
  id: 26632,
  title: 'even',
  description: 'fooey yet',
};

export const sampleWithNewData: NewTicketSubjects = {
  title: 'cleverly regular',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
