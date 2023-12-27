import { ISession } from 'app/entities/session/session.model';

export interface ISessionType {
  id: number;
  title?: string | null;
  description?: string | null;
  sessions?: ISession[] | null;
}

export type NewSessionType = Omit<ISessionType, 'id'> & { id: null };
