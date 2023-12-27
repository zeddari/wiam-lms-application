import { ICourse } from 'app/entities/course/course.model';

export interface ITopic {
  id: number;
  titleAr?: string | null;
  titleLat?: string | null;
  description?: string | null;
  courses?: ICourse[] | null;
  topics?: ITopic[] | null;
  topic2?: ITopic | null;
}

export type NewTopic = Omit<ITopic, 'id'> & { id: null };
