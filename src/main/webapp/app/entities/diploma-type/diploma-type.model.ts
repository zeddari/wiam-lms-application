import { IDiploma } from 'app/entities/diploma/diploma.model';

export interface IDiplomaType {
  id: number;
  titleAr?: string | null;
  titleLat?: string | null;
  diplomas?: IDiploma[] | null;
}

export type NewDiplomaType = Omit<IDiplomaType, 'id'> & { id: null };
