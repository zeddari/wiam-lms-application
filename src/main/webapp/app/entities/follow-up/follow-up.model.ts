import { ICoteryHistory } from 'app/entities/cotery-history/cotery-history.model';
import { Sourate } from 'app/entities/enumerations/sourate.model';
import { Tilawa } from 'app/entities/enumerations/tilawa.model';

export interface IFollowUp {
  id: number;
  fromSourate?: keyof typeof Sourate | null;
  fromAya?: number | null;
  toSourate?: keyof typeof Sourate | null;
  toAya?: number | null;
  tilawaType?: keyof typeof Tilawa | null;
  notation?: string | null;
  remarks?: string | null;
  coteryHistory?: ICoteryHistory | null;
}

export type NewFollowUp = Omit<IFollowUp, 'id'> & { id: null };
