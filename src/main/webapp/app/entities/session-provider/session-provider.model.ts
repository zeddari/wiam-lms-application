import { ISessionLink } from 'app/entities/session-link/session-link.model';

export interface ISessionProvider {
  id: number;
  name?: string | null;
  description?: string | null;
  website?: string | null;
  sessionLinks?: ISessionLink[] | null;
}

export type NewSessionProvider = Omit<ISessionProvider, 'id'> & { id: null };
