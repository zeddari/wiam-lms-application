import { ITickets } from 'app/entities/tickets/tickets.model';

export interface ITicketSubjects {
  id: number;
  title?: string | null;
  description?: string | null;
  tickets?: ITickets[] | null;
}

export type NewTicketSubjects = Omit<ITicketSubjects, 'id'> & { id: null };
