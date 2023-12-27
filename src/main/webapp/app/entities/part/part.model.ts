import { ISession } from 'app/entities/session/session.model';
import { IReview } from 'app/entities/review/review.model';
import { IQuizCertificate } from 'app/entities/quiz-certificate/quiz-certificate.model';
import { ICourse } from 'app/entities/course/course.model';

export interface IPart {
  id: number;
  titleAr?: string | null;
  titleLat?: string | null;
  description?: string | null;
  duration?: number | null;
  imageLink?: string | null;
  imageLinkContentType?: string | null;
  videoLink?: string | null;
  sessions?: ISession[] | null;
  reviews?: IReview[] | null;
  parts?: IPart[] | null;
  quizCertificates?: IQuizCertificate[] | null;
  course?: ICourse | null;
  part1?: IPart | null;
}

export type NewPart = Omit<IPart, 'id'> & { id: null };
