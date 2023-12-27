import { IStudent } from 'app/entities/student/student.model';
import { IQuestion } from 'app/entities/question/question.model';
import { IPart } from 'app/entities/part/part.model';
import { ISession } from 'app/entities/session/session.model';
import { IQuizCertificateType } from 'app/entities/quiz-certificate-type/quiz-certificate-type.model';

export interface IQuizCertificate {
  id: number;
  title?: string | null;
  description?: string | null;
  isActive?: boolean | null;
  students?: IStudent[] | null;
  questions?: IQuestion[] | null;
  part?: IPart | null;
  session?: ISession | null;
  type?: IQuizCertificateType | null;
}

export type NewQuizCertificate = Omit<IQuizCertificate, 'id'> & { id: null };
