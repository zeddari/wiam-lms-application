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
import { IDiploma, NewDiploma } from '../diploma.model';

export type PartialUpdateDiploma = Partial<IDiploma> & Pick<IDiploma, 'id'>;

type RestOf<T extends IDiploma | NewDiploma> = Omit<T, 'graduationDate'> & {
  graduationDate?: string | null;
};

export type RestDiploma = RestOf<IDiploma>;

export type NewRestDiploma = RestOf<NewDiploma>;

export type PartialUpdateRestDiploma = RestOf<PartialUpdateDiploma>;

export type EntityResponseType = HttpResponse<IDiploma>;
export type EntityArrayResponseType = HttpResponse<IDiploma[]>;

@Injectable({ providedIn: 'root' })
export class DiplomaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/diplomas');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/diplomas/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(diploma: NewDiploma): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(diploma);
    return this.http
      .post<RestDiploma>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(diploma: IDiploma): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(diploma);
    return this.http
      .put<RestDiploma>(`${this.resourceUrl}/${this.getDiplomaIdentifier(diploma)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(diploma: PartialUpdateDiploma): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(diploma);
    return this.http
      .patch<RestDiploma>(`${this.resourceUrl}/${this.getDiplomaIdentifier(diploma)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDiploma>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDiploma[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestDiploma[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<IDiploma[]>()], asapScheduler)),
    );
  }

  getDiplomaIdentifier(diploma: Pick<IDiploma, 'id'>): number {
    return diploma.id;
  }

  compareDiploma(o1: Pick<IDiploma, 'id'> | null, o2: Pick<IDiploma, 'id'> | null): boolean {
    return o1 && o2 ? this.getDiplomaIdentifier(o1) === this.getDiplomaIdentifier(o2) : o1 === o2;
  }

  addDiplomaToCollectionIfMissing<Type extends Pick<IDiploma, 'id'>>(
    diplomaCollection: Type[],
    ...diplomasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const diplomas: Type[] = diplomasToCheck.filter(isPresent);
    if (diplomas.length > 0) {
      const diplomaCollectionIdentifiers = diplomaCollection.map(diplomaItem => this.getDiplomaIdentifier(diplomaItem)!);
      const diplomasToAdd = diplomas.filter(diplomaItem => {
        const diplomaIdentifier = this.getDiplomaIdentifier(diplomaItem);
        if (diplomaCollectionIdentifiers.includes(diplomaIdentifier)) {
          return false;
        }
        diplomaCollectionIdentifiers.push(diplomaIdentifier);
        return true;
      });
      return [...diplomasToAdd, ...diplomaCollection];
    }
    return diplomaCollection;
  }

  protected convertDateFromClient<T extends IDiploma | NewDiploma | PartialUpdateDiploma>(diploma: T): RestOf<T> {
    return {
      ...diploma,
      graduationDate: diploma.graduationDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restDiploma: RestDiploma): IDiploma {
    return {
      ...restDiploma,
      graduationDate: restDiploma.graduationDate ? dayjs(restDiploma.graduationDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDiploma>): HttpResponse<IDiploma> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDiploma[]>): HttpResponse<IDiploma[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
