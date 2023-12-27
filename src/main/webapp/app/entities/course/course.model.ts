import dayjs from 'dayjs/esm';
import { IPart } from 'app/entities/part/part.model';
import { IEnrolement } from 'app/entities/enrolement/enrolement.model';
import { IQuestion } from 'app/entities/question/question.model';
import { ITopic } from 'app/entities/topic/topic.model';
import { ILevel } from 'app/entities/level/level.model';
import { IProfessor } from 'app/entities/professor/professor.model';

export interface ICourse {
  id: number;
  titleAr?: string | null;
  titleLat?: string | null;
  description?: string | null;
  subTitles?: string | null;
  requirement?: string | null;
  duration?: number | null;
  option?: string | null;
  type?: boolean | null;
  imageLink?: string | null;
  imageLinkContentType?: string | null;
  videoLink?: string | null;
  price?: number | null;
  isActive?: boolean | null;
  activateAt?: dayjs.Dayjs | null;
  isConfirmed?: boolean | null;
  confirmedAt?: dayjs.Dayjs | null;
  parts?: IPart[] | null;
  enrolements?: IEnrolement[] | null;
  questions?: IQuestion[] | null;
  topic1?: ITopic | null;
  level?: ILevel | null;
  professor1?: IProfessor | null;
}

export type NewCourse = Omit<ICourse, 'id'> & { id: null };
