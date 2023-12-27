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
import { IUserCustom, NewUserCustom } from '../user-custom.model';

export type PartialUpdateUserCustom = Partial<IUserCustom> & Pick<IUserCustom, 'id'>;

type RestOf<T extends IUserCustom | NewUserCustom> = Omit<T, 'birthDay' | 'creationDate' | 'modificationDate' | 'deletionDate'> & {
  birthDay?: string | null;
  creationDate?: string | null;
  modificationDate?: string | null;
  deletionDate?: string | null;
};

export type RestUserCustom = RestOf<IUserCustom>;

export type NewRestUserCustom = RestOf<NewUserCustom>;

export type PartialUpdateRestUserCustom = RestOf<PartialUpdateUserCustom>;

export type EntityResponseType = HttpResponse<IUserCustom>;
export type EntityArrayResponseType = HttpResponse<IUserCustom[]>;

@Injectable({ providedIn: 'root' })
export class UserCustomService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/user-customs');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/user-customs/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(userCustom: NewUserCustom): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userCustom);
    return this.http
      .post<RestUserCustom>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(userCustom: IUserCustom): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userCustom);
    return this.http
      .put<RestUserCustom>(`${this.resourceUrl}/${this.getUserCustomIdentifier(userCustom)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(userCustom: PartialUpdateUserCustom): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(userCustom);
    return this.http
      .patch<RestUserCustom>(`${this.resourceUrl}/${this.getUserCustomIdentifier(userCustom)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestUserCustom>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestUserCustom[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestUserCustom[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<IUserCustom[]>()], asapScheduler)),
    );
  }

  getUserCustomIdentifier(userCustom: Pick<IUserCustom, 'id'>): number {
    return userCustom.id;
  }

  compareUserCustom(o1: Pick<IUserCustom, 'id'> | null, o2: Pick<IUserCustom, 'id'> | null): boolean {
    return o1 && o2 ? this.getUserCustomIdentifier(o1) === this.getUserCustomIdentifier(o2) : o1 === o2;
  }

  addUserCustomToCollectionIfMissing<Type extends Pick<IUserCustom, 'id'>>(
    userCustomCollection: Type[],
    ...userCustomsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const userCustoms: Type[] = userCustomsToCheck.filter(isPresent);
    if (userCustoms.length > 0) {
      const userCustomCollectionIdentifiers = userCustomCollection.map(userCustomItem => this.getUserCustomIdentifier(userCustomItem)!);
      const userCustomsToAdd = userCustoms.filter(userCustomItem => {
        const userCustomIdentifier = this.getUserCustomIdentifier(userCustomItem);
        if (userCustomCollectionIdentifiers.includes(userCustomIdentifier)) {
          return false;
        }
        userCustomCollectionIdentifiers.push(userCustomIdentifier);
        return true;
      });
      return [...userCustomsToAdd, ...userCustomCollection];
    }
    return userCustomCollection;
  }

  protected convertDateFromClient<T extends IUserCustom | NewUserCustom | PartialUpdateUserCustom>(userCustom: T): RestOf<T> {
    return {
      ...userCustom,
      birthDay: userCustom.birthDay?.format(DATE_FORMAT) ?? null,
      creationDate: userCustom.creationDate?.toJSON() ?? null,
      modificationDate: userCustom.modificationDate?.toJSON() ?? null,
      deletionDate: userCustom.deletionDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restUserCustom: RestUserCustom): IUserCustom {
    return {
      ...restUserCustom,
      birthDay: restUserCustom.birthDay ? dayjs(restUserCustom.birthDay) : undefined,
      creationDate: restUserCustom.creationDate ? dayjs(restUserCustom.creationDate) : undefined,
      modificationDate: restUserCustom.modificationDate ? dayjs(restUserCustom.modificationDate) : undefined,
      deletionDate: restUserCustom.deletionDate ? dayjs(restUserCustom.deletionDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestUserCustom>): HttpResponse<IUserCustom> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestUserCustom[]>): HttpResponse<IUserCustom[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
