import dayjs from 'dayjs/esm';
import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { ITicketSubjects } from 'app/entities/ticket-subjects/ticket-subjects.model';

export interface ITickets {
  id: number;
  title?: string | null;
  description?: string | null;
  justifDoc?: string | null;
  justifDocContentType?: string | null;
  dateTicket?: dayjs.Dayjs | null;
  dateProcess?: dayjs.Dayjs | null;
  processed?: boolean | null;
  userCustom?: IUserCustom | null;
  subject?: ITicketSubjects | null;
}

export type NewTickets = Omit<ITickets, 'id'> & { id: null };
