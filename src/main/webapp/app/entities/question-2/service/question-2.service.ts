import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IQuestion2, NewQuestion2 } from '../question-2.model';

export type PartialUpdateQuestion2 = Partial<IQuestion2> & Pick<IQuestion2, 'id'>;

export type EntityResponseType = HttpResponse<IQuestion2>;
export type EntityArrayResponseType = HttpResponse<IQuestion2[]>;

@Injectable({ providedIn: 'root' })
export class Question2Service {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/question-2-s');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/question-2-s/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(question2: NewQuestion2): Observable<EntityResponseType> {
    return this.http.post<IQuestion2>(this.resourceUrl, question2, { observe: 'response' });
  }

  update(question2: IQuestion2): Observable<EntityResponseType> {
    return this.http.put<IQuestion2>(`${this.resourceUrl}/${this.getQuestion2Identifier(question2)}`, question2, { observe: 'response' });
  }

  partialUpdate(question2: PartialUpdateQuestion2): Observable<EntityResponseType> {
    return this.http.patch<IQuestion2>(`${this.resourceUrl}/${this.getQuestion2Identifier(question2)}`, question2, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IQuestion2>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IQuestion2[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IQuestion2[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IQuestion2[]>()], asapScheduler)));
  }

  getQuestion2Identifier(question2: Pick<IQuestion2, 'id'>): number {
    return question2.id;
  }

  compareQuestion2(o1: Pick<IQuestion2, 'id'> | null, o2: Pick<IQuestion2, 'id'> | null): boolean {
    return o1 && o2 ? this.getQuestion2Identifier(o1) === this.getQuestion2Identifier(o2) : o1 === o2;
  }

  addQuestion2ToCollectionIfMissing<Type extends Pick<IQuestion2, 'id'>>(
    question2Collection: Type[],
    ...question2sToCheck: (Type | null | undefined)[]
  ): Type[] {
    const question2s: Type[] = question2sToCheck.filter(isPresent);
    if (question2s.length > 0) {
      const question2CollectionIdentifiers = question2Collection.map(question2Item => this.getQuestion2Identifier(question2Item)!);
      const question2sToAdd = question2s.filter(question2Item => {
        const question2Identifier = this.getQuestion2Identifier(question2Item);
        if (question2CollectionIdentifiers.includes(question2Identifier)) {
          return false;
        }
        question2CollectionIdentifiers.push(question2Identifier);
        return true;
      });
      return [...question2sToAdd, ...question2Collection];
    }
    return question2Collection;
  }
}
