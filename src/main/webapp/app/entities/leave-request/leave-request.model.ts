import dayjs from 'dayjs/esm';

export interface ILeaveRequest {
  id: number;
  title?: string | null;
  from?: dayjs.Dayjs | null;
  toDate?: dayjs.Dayjs | null;
  details?: string | null;
}

export type NewLeaveRequest = Omit<ILeaveRequest, 'id'> & { id: null };
