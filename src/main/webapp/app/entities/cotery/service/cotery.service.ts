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
import { ICotery, NewCotery } from '../cotery.model';

export type PartialUpdateCotery = Partial<ICotery> & Pick<ICotery, 'id'>;

type RestOf<T extends ICotery | NewCotery> = Omit<T, 'date'> & {
  date?: string | null;
};

export type RestCotery = RestOf<ICotery>;

export type NewRestCotery = RestOf<NewCotery>;

export type PartialUpdateRestCotery = RestOf<PartialUpdateCotery>;

export type EntityResponseType = HttpResponse<ICotery>;
export type EntityArrayResponseType = HttpResponse<ICotery[]>;

@Injectable({ providedIn: 'root' })
export class CoteryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/coteries');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/coteries/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(cotery: NewCotery): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cotery);
    return this.http
      .post<RestCotery>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(cotery: ICotery): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cotery);
    return this.http
      .put<RestCotery>(`${this.resourceUrl}/${this.getCoteryIdentifier(cotery)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(cotery: PartialUpdateCotery): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(cotery);
    return this.http
      .patch<RestCotery>(`${this.resourceUrl}/${this.getCoteryIdentifier(cotery)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCotery>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCotery[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestCotery[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<ICotery[]>()], asapScheduler)),
    );
  }

  getCoteryIdentifier(cotery: Pick<ICotery, 'id'>): number {
    return cotery.id;
  }

  compareCotery(o1: Pick<ICotery, 'id'> | null, o2: Pick<ICotery, 'id'> | null): boolean {
    return o1 && o2 ? this.getCoteryIdentifier(o1) === this.getCoteryIdentifier(o2) : o1 === o2;
  }

  addCoteryToCollectionIfMissing<Type extends Pick<ICotery, 'id'>>(
    coteryCollection: Type[],
    ...coteriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const coteries: Type[] = coteriesToCheck.filter(isPresent);
    if (coteries.length > 0) {
      const coteryCollectionIdentifiers = coteryCollection.map(coteryItem => this.getCoteryIdentifier(coteryItem)!);
      const coteriesToAdd = coteries.filter(coteryItem => {
        const coteryIdentifier = this.getCoteryIdentifier(coteryItem);
        if (coteryCollectionIdentifiers.includes(coteryIdentifier)) {
          return false;
        }
        coteryCollectionIdentifiers.push(coteryIdentifier);
        return true;
      });
      return [...coteriesToAdd, ...coteryCollection];
    }
    return coteryCollection;
  }

  protected convertDateFromClient<T extends ICotery | NewCotery | PartialUpdateCotery>(cotery: T): RestOf<T> {
    return {
      ...cotery,
      date: cotery.date?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restCotery: RestCotery): ICotery {
    return {
      ...restCotery,
      date: restCotery.date ? dayjs(restCotery.date) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCotery>): HttpResponse<ICotery> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCotery[]>): HttpResponse<ICotery[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
