import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { IPart } from 'app/entities/part/part.model';

export interface IReview {
  id: number;
  body?: string | null;
  rating?: number | null;
  userCustom?: IUserCustom | null;
  course?: IPart | null;
}

export type NewReview = Omit<IReview, 'id'> & { id: null };
