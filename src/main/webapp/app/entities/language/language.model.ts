import { IUserCustom } from 'app/entities/user-custom/user-custom.model';

export interface ILanguage {
  id: number;
  label?: string | null;
  userCustom?: IUserCustom | null;
}

export type NewLanguage = Omit<ILanguage, 'id'> & { id: null };
