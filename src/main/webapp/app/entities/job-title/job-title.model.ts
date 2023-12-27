import { IEmployee } from 'app/entities/employee/employee.model';

export interface IJobTitle {
  id: number;
  titleAr?: string | null;
  titleLat?: string | null;
  description?: string | null;
  employees?: IEmployee[] | null;
}

export type NewJobTitle = Omit<IJobTitle, 'id'> & { id: null };
