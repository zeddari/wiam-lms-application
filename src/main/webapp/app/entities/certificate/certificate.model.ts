import { IStudent } from 'app/entities/student/student.model';
import { ICotery } from 'app/entities/cotery/cotery.model';
import { CertificateType } from 'app/entities/enumerations/certificate-type.model';

export interface ICertificate {
  id: number;
  coteryName?: string | null;
  studentFullName?: string | null;
  certificateType?: keyof typeof CertificateType | null;
  student?: IStudent | null;
  cotery?: ICotery | null;
}

export type NewCertificate = Omit<ICertificate, 'id'> & { id: null };
