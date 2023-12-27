import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICertificate, NewCertificate } from '../certificate.model';

export type PartialUpdateCertificate = Partial<ICertificate> & Pick<ICertificate, 'id'>;

export type EntityResponseType = HttpResponse<ICertificate>;
export type EntityArrayResponseType = HttpResponse<ICertificate[]>;

@Injectable({ providedIn: 'root' })
export class CertificateService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/certificates');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/certificates/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(certificate: NewCertificate): Observable<EntityResponseType> {
    return this.http.post<ICertificate>(this.resourceUrl, certificate, { observe: 'response' });
  }

  update(certificate: ICertificate): Observable<EntityResponseType> {
    return this.http.put<ICertificate>(`${this.resourceUrl}/${this.getCertificateIdentifier(certificate)}`, certificate, {
      observe: 'response',
    });
  }

  partialUpdate(certificate: PartialUpdateCertificate): Observable<EntityResponseType> {
    return this.http.patch<ICertificate>(`${this.resourceUrl}/${this.getCertificateIdentifier(certificate)}`, certificate, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICertificate>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICertificate[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICertificate[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ICertificate[]>()], asapScheduler)));
  }

  getCertificateIdentifier(certificate: Pick<ICertificate, 'id'>): number {
    return certificate.id;
  }

  compareCertificate(o1: Pick<ICertificate, 'id'> | null, o2: Pick<ICertificate, 'id'> | null): boolean {
    return o1 && o2 ? this.getCertificateIdentifier(o1) === this.getCertificateIdentifier(o2) : o1 === o2;
  }

  addCertificateToCollectionIfMissing<Type extends Pick<ICertificate, 'id'>>(
    certificateCollection: Type[],
    ...certificatesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const certificates: Type[] = certificatesToCheck.filter(isPresent);
    if (certificates.length > 0) {
      const certificateCollectionIdentifiers = certificateCollection.map(
        certificateItem => this.getCertificateIdentifier(certificateItem)!,
      );
      const certificatesToAdd = certificates.filter(certificateItem => {
        const certificateIdentifier = this.getCertificateIdentifier(certificateItem);
        if (certificateCollectionIdentifiers.includes(certificateIdentifier)) {
          return false;
        }
        certificateCollectionIdentifiers.push(certificateIdentifier);
        return true;
      });
      return [...certificatesToAdd, ...certificateCollection];
    }
    return certificateCollection;
  }
}
