import dayjs from 'dayjs/esm';
import { ISession } from 'app/entities/session/session.model';
import { IStudent } from 'app/entities/student/student.model';
import { IProgressionMode } from 'app/entities/progression-mode/progression-mode.model';

export interface IProgression {
  id: number;
  status?: boolean | null;
  isJustified?: boolean | null;
  justifRef?: string | null;
  lateArrival?: number | null;
  earlyDeparture?: number | null;
  taskDone?: boolean | null;
  grade1?: string | null;
  description?: string | null;
  taskStart?: number | null;
  taskEnd?: number | null;
  taskStep?: number | null;
  progressionDate?: dayjs.Dayjs | null;
  session?: ISession | null;
  student1?: IStudent | null;
  mode?: IProgressionMode | null;
}

export type NewProgression = Omit<IProgression, 'id'> & { id: null };
