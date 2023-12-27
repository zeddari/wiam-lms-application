import { ISession } from 'app/entities/session/session.model';

export interface ISessionMode {
  id: number;
  title?: string | null;
  description?: string | null;
  sessions?: ISession[] | null;
}

export type NewSessionMode = Omit<ISessionMode, 'id'> & { id: null };
