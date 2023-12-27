import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICoteryHistory, NewCoteryHistory } from '../cotery-history.model';

export type PartialUpdateCoteryHistory = Partial<ICoteryHistory> & Pick<ICoteryHistory, 'id'>;

type RestOf<T extends ICoteryHistory | NewCoteryHistory> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestCoteryHistory = RestOf<ICoteryHistory>;

export type NewRestCoteryHistory = RestOf<NewCoteryHistory>;

export type PartialUpdateRestCoteryHistory = RestOf<PartialUpdateCoteryHistory>;

export type EntityResponseType = HttpResponse<ICoteryHistory>;
export type EntityArrayResponseType = HttpResponse<ICoteryHistory[]>;

@Injectable({ providedIn: 'root' })
export class CoteryHistoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/cotery-histories');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/cotery-histories/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(coteryHistory: NewCoteryHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(coteryHistory);
    return this.http
      .post<RestCoteryHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(coteryHistory: ICoteryHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(coteryHistory);
    return this.http
      .put<RestCoteryHistory>(`${this.resourceUrl}/${this.getCoteryHistoryIdentifier(coteryHistory)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(coteryHistory: PartialUpdateCoteryHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(coteryHistory);
    return this.http
      .patch<RestCoteryHistory>(`${this.resourceUrl}/${this.getCoteryHistoryIdentifier(coteryHistory)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCoteryHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCoteryHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestCoteryHistory[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<ICoteryHistory[]>()], asapScheduler)),
    );
  }

  getCoteryHistoryIdentifier(coteryHistory: Pick<ICoteryHistory, 'id'>): number {
    return coteryHistory.id;
  }

  compareCoteryHistory(o1: Pick<ICoteryHistory, 'id'> | null, o2: Pick<ICoteryHistory, 'id'> | null): boolean {
    return o1 && o2 ? this.getCoteryHistoryIdentifier(o1) === this.getCoteryHistoryIdentifier(o2) : o1 === o2;
  }

  addCoteryHistoryToCollectionIfMissing<Type extends Pick<ICoteryHistory, 'id'>>(
    coteryHistoryCollection: Type[],
    ...coteryHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const coteryHistories: Type[] = coteryHistoriesToCheck.filter(isPresent);
    if (coteryHistories.length > 0) {
      const coteryHistoryCollectionIdentifiers = coteryHistoryCollection.map(
        coteryHistoryItem => this.getCoteryHistoryIdentifier(coteryHistoryItem)!,
      );
      const coteryHistoriesToAdd = coteryHistories.filter(coteryHistoryItem => {
        const coteryHistoryIdentifier = this.getCoteryHistoryIdentifier(coteryHistoryItem);
        if (coteryHistoryCollectionIdentifiers.includes(coteryHistoryIdentifier)) {
          return false;
        }
        coteryHistoryCollectionIdentifiers.push(coteryHistoryIdentifier);
        return true;
      });
      return [...coteryHistoriesToAdd, ...coteryHistoryCollection];
    }
    return coteryHistoryCollection;
  }

  protected convertDateFromClient<T extends ICoteryHistory | NewCoteryHistory | PartialUpdateCoteryHistory>(coteryHistory: T): RestOf<T> {
    return {
      ...coteryHistory,
      date: coteryHistory.date?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restCoteryHistory: RestCoteryHistory): ICoteryHistory {
    return {
      ...restCoteryHistory,
      date: restCoteryHistory.date ? dayjs(restCoteryHistory.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCoteryHistory>): HttpResponse<ICoteryHistory> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCoteryHistory[]>): HttpResponse<ICoteryHistory[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
