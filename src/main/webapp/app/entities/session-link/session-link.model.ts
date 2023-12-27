import { ISessionProvider } from 'app/entities/session-provider/session-provider.model';
import { ISession } from 'app/entities/session/session.model';

export interface ISessionLink {
  id: number;
  title?: string | null;
  description?: string | null;
  link?: string | null;
  provider?: ISessionProvider | null;
  sessions?: ISession[] | null;
}

export type NewSessionLink = Omit<ISessionLink, 'id'> & { id: null };
