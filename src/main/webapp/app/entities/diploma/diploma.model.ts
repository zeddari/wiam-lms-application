import dayjs from 'dayjs/esm';
import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { IDiplomaType } from 'app/entities/diploma-type/diploma-type.model';

export interface IDiploma {
  id: number;
  title?: string | null;
  subject?: string | null;
  detail?: string | null;
  supervisor?: string | null;
  grade?: string | null;
  graduationDate?: dayjs.Dayjs | null;
  school?: string | null;
  userCustom?: IUserCustom | null;
  diplomaType?: IDiplomaType | null;
}

export type NewDiploma = Omit<IDiploma, 'id'> & { id: null };
