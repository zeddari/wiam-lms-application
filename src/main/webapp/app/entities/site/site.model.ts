import { IClassroom } from 'app/entities/classroom/classroom.model';
import { ICity } from 'app/entities/city/city.model';

export interface ISite {
  id: number;
  nameAr?: string | null;
  nameLat?: string | null;
  description?: string | null;
  localisation?: string | null;
  classrooms?: IClassroom[] | null;
  city?: ICity | null;
}

export type NewSite = Omit<ISite, 'id'> & { id: null };
