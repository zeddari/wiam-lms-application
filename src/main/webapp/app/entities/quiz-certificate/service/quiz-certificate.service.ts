import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IQuizCertificate, NewQuizCertificate } from '../quiz-certificate.model';

export type PartialUpdateQuizCertificate = Partial<IQuizCertificate> & Pick<IQuizCertificate, 'id'>;

export type EntityResponseType = HttpResponse<IQuizCertificate>;
export type EntityArrayResponseType = HttpResponse<IQuizCertificate[]>;

@Injectable({ providedIn: 'root' })
export class QuizCertificateService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/quiz-certificates');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/quiz-certificates/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(quizCertificate: NewQuizCertificate): Observable<EntityResponseType> {
    return this.http.post<IQuizCertificate>(this.resourceUrl, quizCertificate, { observe: 'response' });
  }

  update(quizCertificate: IQuizCertificate): Observable<EntityResponseType> {
    return this.http.put<IQuizCertificate>(`${this.resourceUrl}/${this.getQuizCertificateIdentifier(quizCertificate)}`, quizCertificate, {
      observe: 'response',
    });
  }

  partialUpdate(quizCertificate: PartialUpdateQuizCertificate): Observable<EntityResponseType> {
    return this.http.patch<IQuizCertificate>(`${this.resourceUrl}/${this.getQuizCertificateIdentifier(quizCertificate)}`, quizCertificate, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IQuizCertificate>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IQuizCertificate[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IQuizCertificate[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IQuizCertificate[]>()], asapScheduler)));
  }

  getQuizCertificateIdentifier(quizCertificate: Pick<IQuizCertificate, 'id'>): number {
    return quizCertificate.id;
  }

  compareQuizCertificate(o1: Pick<IQuizCertificate, 'id'> | null, o2: Pick<IQuizCertificate, 'id'> | null): boolean {
    return o1 && o2 ? this.getQuizCertificateIdentifier(o1) === this.getQuizCertificateIdentifier(o2) : o1 === o2;
  }

  addQuizCertificateToCollectionIfMissing<Type extends Pick<IQuizCertificate, 'id'>>(
    quizCertificateCollection: Type[],
    ...quizCertificatesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const quizCertificates: Type[] = quizCertificatesToCheck.filter(isPresent);
    if (quizCertificates.length > 0) {
      const quizCertificateCollectionIdentifiers = quizCertificateCollection.map(
        quizCertificateItem => this.getQuizCertificateIdentifier(quizCertificateItem)!,
      );
      const quizCertificatesToAdd = quizCertificates.filter(quizCertificateItem => {
        const quizCertificateIdentifier = this.getQuizCertificateIdentifier(quizCertificateItem);
        if (quizCertificateCollectionIdentifiers.includes(quizCertificateIdentifier)) {
          return false;
        }
        quizCertificateCollectionIdentifiers.push(quizCertificateIdentifier);
        return true;
      });
      return [...quizCertificatesToAdd, ...quizCertificateCollection];
    }
    return quizCertificateCollection;
  }
}
