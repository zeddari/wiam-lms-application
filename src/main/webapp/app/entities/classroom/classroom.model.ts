import { ISession } from 'app/entities/session/session.model';
import { ISite } from 'app/entities/site/site.model';

export interface IClassroom {
  id: number;
  nameAr?: string | null;
  nameLat?: string | null;
  description?: string | null;
  sessions?: ISession[] | null;
  site?: ISite | null;
}

export type NewClassroom = Omit<IClassroom, 'id'> & { id: null };
