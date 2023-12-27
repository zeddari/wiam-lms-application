import { IQuizCertificate } from 'app/entities/quiz-certificate/quiz-certificate.model';

export interface IQuizCertificateType {
  id: number;
  titleAr?: string | null;
  titleLat?: string | null;
  quizCertificates?: IQuizCertificate[] | null;
}

export type NewQuizCertificateType = Omit<IQuizCertificateType, 'id'> & { id: null };
