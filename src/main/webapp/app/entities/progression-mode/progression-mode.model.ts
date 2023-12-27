import { IProgression } from 'app/entities/progression/progression.model';

export interface IProgressionMode {
  id: number;
  titleAr?: string | null;
  titleLat?: string | null;
  progressions?: IProgression[] | null;
}

export type NewProgressionMode = Omit<IProgressionMode, 'id'> & { id: null };
