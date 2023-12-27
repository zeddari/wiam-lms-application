import { ICourse } from 'app/entities/course/course.model';

export interface ILevel {
  id: number;
  titleAr?: string | null;
  titleLat?: string | null;
  courses?: ICourse[] | null;
}

export type NewLevel = Omit<ILevel, 'id'> & { id: null };
