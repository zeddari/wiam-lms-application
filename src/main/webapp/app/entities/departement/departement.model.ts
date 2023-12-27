import { IEmployee } from 'app/entities/employee/employee.model';

export interface IDepartement {
  id: number;
  nameAr?: string | null;
  nameLat?: string | null;
  description?: string | null;
  employees?: IEmployee[] | null;
  departements?: IDepartement[] | null;
  departement1?: IDepartement | null;
}

export type NewDepartement = Omit<IDepartement, 'id'> & { id: null };
