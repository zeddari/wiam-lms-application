import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IEnrolement, NewEnrolement } from '../enrolement.model';

export type PartialUpdateEnrolement = Partial<IEnrolement> & Pick<IEnrolement, 'id'>;

type RestOf<T extends IEnrolement | NewEnrolement> = Omit<T, 'activatedAt' | 'activatedBy' | 'enrolmentStartTime' | 'enrolemntEndTime'> & {
  activatedAt?: string | null;
  activatedBy?: string | null;
  enrolmentStartTime?: string | null;
  enrolemntEndTime?: string | null;
};

export type RestEnrolement = RestOf<IEnrolement>;

export type NewRestEnrolement = RestOf<NewEnrolement>;

export type PartialUpdateRestEnrolement = RestOf<PartialUpdateEnrolement>;

export type EntityResponseType = HttpResponse<IEnrolement>;
export type EntityArrayResponseType = HttpResponse<IEnrolement[]>;

@Injectable({ providedIn: 'root' })
export class EnrolementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/enrolements');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/enrolements/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(enrolement: NewEnrolement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(enrolement);
    return this.http
      .post<RestEnrolement>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(enrolement: IEnrolement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(enrolement);
    return this.http
      .put<RestEnrolement>(`${this.resourceUrl}/${this.getEnrolementIdentifier(enrolement)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(enrolement: PartialUpdateEnrolement): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(enrolement);
    return this.http
      .patch<RestEnrolement>(`${this.resourceUrl}/${this.getEnrolementIdentifier(enrolement)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEnrolement>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEnrolement[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestEnrolement[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<IEnrolement[]>()], asapScheduler)),
    );
  }

  getEnrolementIdentifier(enrolement: Pick<IEnrolement, 'id'>): number {
    return enrolement.id;
  }

  compareEnrolement(o1: Pick<IEnrolement, 'id'> | null, o2: Pick<IEnrolement, 'id'> | null): boolean {
    return o1 && o2 ? this.getEnrolementIdentifier(o1) === this.getEnrolementIdentifier(o2) : o1 === o2;
  }

  addEnrolementToCollectionIfMissing<Type extends Pick<IEnrolement, 'id'>>(
    enrolementCollection: Type[],
    ...enrolementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const enrolements: Type[] = enrolementsToCheck.filter(isPresent);
    if (enrolements.length > 0) {
      const enrolementCollectionIdentifiers = enrolementCollection.map(enrolementItem => this.getEnrolementIdentifier(enrolementItem)!);
      const enrolementsToAdd = enrolements.filter(enrolementItem => {
        const enrolementIdentifier = this.getEnrolementIdentifier(enrolementItem);
        if (enrolementCollectionIdentifiers.includes(enrolementIdentifier)) {
          return false;
        }
        enrolementCollectionIdentifiers.push(enrolementIdentifier);
        return true;
      });
      return [...enrolementsToAdd, ...enrolementCollection];
    }
    return enrolementCollection;
  }

  protected convertDateFromClient<T extends IEnrolement | NewEnrolement | PartialUpdateEnrolement>(enrolement: T): RestOf<T> {
    return {
      ...enrolement,
      activatedAt: enrolement.activatedAt?.toJSON() ?? null,
      activatedBy: enrolement.activatedBy?.toJSON() ?? null,
      enrolmentStartTime: enrolement.enrolmentStartTime?.toJSON() ?? null,
      enrolemntEndTime: enrolement.enrolemntEndTime?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEnrolement: RestEnrolement): IEnrolement {
    return {
      ...restEnrolement,
      activatedAt: restEnrolement.activatedAt ? dayjs(restEnrolement.activatedAt) : undefined,
      activatedBy: restEnrolement.activatedBy ? dayjs(restEnrolement.activatedBy) : undefined,
      enrolmentStartTime: restEnrolement.enrolmentStartTime ? dayjs(restEnrolement.enrolmentStartTime) : undefined,
      enrolemntEndTime: restEnrolement.enrolemntEndTime ? dayjs(restEnrolement.enrolemntEndTime) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEnrolement>): HttpResponse<IEnrolement> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEnrolement[]>): HttpResponse<IEnrolement[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
