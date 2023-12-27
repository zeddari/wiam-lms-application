import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IQuizCertificateType, NewQuizCertificateType } from '../quiz-certificate-type.model';

export type PartialUpdateQuizCertificateType = Partial<IQuizCertificateType> & Pick<IQuizCertificateType, 'id'>;

export type EntityResponseType = HttpResponse<IQuizCertificateType>;
export type EntityArrayResponseType = HttpResponse<IQuizCertificateType[]>;

@Injectable({ providedIn: 'root' })
export class QuizCertificateTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/quiz-certificate-types');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/quiz-certificate-types/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(quizCertificateType: NewQuizCertificateType): Observable<EntityResponseType> {
    return this.http.post<IQuizCertificateType>(this.resourceUrl, quizCertificateType, { observe: 'response' });
  }

  update(quizCertificateType: IQuizCertificateType): Observable<EntityResponseType> {
    return this.http.put<IQuizCertificateType>(
      `${this.resourceUrl}/${this.getQuizCertificateTypeIdentifier(quizCertificateType)}`,
      quizCertificateType,
      { observe: 'response' },
    );
  }

  partialUpdate(quizCertificateType: PartialUpdateQuizCertificateType): Observable<EntityResponseType> {
    return this.http.patch<IQuizCertificateType>(
      `${this.resourceUrl}/${this.getQuizCertificateTypeIdentifier(quizCertificateType)}`,
      quizCertificateType,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IQuizCertificateType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IQuizCertificateType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IQuizCertificateType[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IQuizCertificateType[]>()], asapScheduler)));
  }

  getQuizCertificateTypeIdentifier(quizCertificateType: Pick<IQuizCertificateType, 'id'>): number {
    return quizCertificateType.id;
  }

  compareQuizCertificateType(o1: Pick<IQuizCertificateType, 'id'> | null, o2: Pick<IQuizCertificateType, 'id'> | null): boolean {
    return o1 && o2 ? this.getQuizCertificateTypeIdentifier(o1) === this.getQuizCertificateTypeIdentifier(o2) : o1 === o2;
  }

  addQuizCertificateTypeToCollectionIfMissing<Type extends Pick<IQuizCertificateType, 'id'>>(
    quizCertificateTypeCollection: Type[],
    ...quizCertificateTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const quizCertificateTypes: Type[] = quizCertificateTypesToCheck.filter(isPresent);
    if (quizCertificateTypes.length > 0) {
      const quizCertificateTypeCollectionIdentifiers = quizCertificateTypeCollection.map(
        quizCertificateTypeItem => this.getQuizCertificateTypeIdentifier(quizCertificateTypeItem)!,
      );
      const quizCertificateTypesToAdd = quizCertificateTypes.filter(quizCertificateTypeItem => {
        const quizCertificateTypeIdentifier = this.getQuizCertificateTypeIdentifier(quizCertificateTypeItem);
        if (quizCertificateTypeCollectionIdentifiers.includes(quizCertificateTypeIdentifier)) {
          return false;
        }
        quizCertificateTypeCollectionIdentifiers.push(quizCertificateTypeIdentifier);
        return true;
      });
      return [...quizCertificateTypesToAdd, ...quizCertificateTypeCollection];
    }
    return quizCertificateTypeCollection;
  }
}
