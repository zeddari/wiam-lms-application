import { ISession } from 'app/entities/session/session.model';

export interface ISessionJoinMode {
  id: number;
  title?: string | null;
  description?: string | null;
  sessions?: ISession[] | null;
}

export type NewSessionJoinMode = Omit<ISessionJoinMode, 'id'> & { id: null };
