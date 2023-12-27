import { ISession } from 'app/entities/session/session.model';
import { IStudent } from 'app/entities/student/student.model';

export interface IGroup {
  id: number;
  nameAr?: string | null;
  nameLat?: string | null;
  description?: string | null;
  sessions?: ISession[] | null;
  students?: IStudent[] | null;
  groups?: IGroup[] | null;
  group1?: IGroup | null;
}

export type NewGroup = Omit<IGroup, 'id'> & { id: null };
