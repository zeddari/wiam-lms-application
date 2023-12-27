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
import { ISponsoring, NewSponsoring } from '../sponsoring.model';

export type PartialUpdateSponsoring = Partial<ISponsoring> & Pick<ISponsoring, 'id'>;

type RestOf<T extends ISponsoring | NewSponsoring> = Omit<T, 'startDate' | 'endDate'> & {
  startDate?: string | null;
  endDate?: string | null;
};

export type RestSponsoring = RestOf<ISponsoring>;

export type NewRestSponsoring = RestOf<NewSponsoring>;

export type PartialUpdateRestSponsoring = RestOf<PartialUpdateSponsoring>;

export type EntityResponseType = HttpResponse<ISponsoring>;
export type EntityArrayResponseType = HttpResponse<ISponsoring[]>;

@Injectable({ providedIn: 'root' })
export class SponsoringService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sponsorings');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/sponsorings/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(sponsoring: NewSponsoring): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sponsoring);
    return this.http
      .post<RestSponsoring>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(sponsoring: ISponsoring): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sponsoring);
    return this.http
      .put<RestSponsoring>(`${this.resourceUrl}/${this.getSponsoringIdentifier(sponsoring)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(sponsoring: PartialUpdateSponsoring): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sponsoring);
    return this.http
      .patch<RestSponsoring>(`${this.resourceUrl}/${this.getSponsoringIdentifier(sponsoring)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSponsoring>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSponsoring[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestSponsoring[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<ISponsoring[]>()], asapScheduler)),
    );
  }

  getSponsoringIdentifier(sponsoring: Pick<ISponsoring, 'id'>): number {
    return sponsoring.id;
  }

  compareSponsoring(o1: Pick<ISponsoring, 'id'> | null, o2: Pick<ISponsoring, 'id'> | null): boolean {
    return o1 && o2 ? this.getSponsoringIdentifier(o1) === this.getSponsoringIdentifier(o2) : o1 === o2;
  }

  addSponsoringToCollectionIfMissing<Type extends Pick<ISponsoring, 'id'>>(
    sponsoringCollection: Type[],
    ...sponsoringsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sponsorings: Type[] = sponsoringsToCheck.filter(isPresent);
    if (sponsorings.length > 0) {
      const sponsoringCollectionIdentifiers = sponsoringCollection.map(sponsoringItem => this.getSponsoringIdentifier(sponsoringItem)!);
      const sponsoringsToAdd = sponsorings.filter(sponsoringItem => {
        const sponsoringIdentifier = this.getSponsoringIdentifier(sponsoringItem);
        if (sponsoringCollectionIdentifiers.includes(sponsoringIdentifier)) {
          return false;
        }
        sponsoringCollectionIdentifiers.push(sponsoringIdentifier);
        return true;
      });
      return [...sponsoringsToAdd, ...sponsoringCollection];
    }
    return sponsoringCollection;
  }

  protected convertDateFromClient<T extends ISponsoring | NewSponsoring | PartialUpdateSponsoring>(sponsoring: T): RestOf<T> {
    return {
      ...sponsoring,
      startDate: sponsoring.startDate?.format(DATE_FORMAT) ?? null,
      endDate: sponsoring.endDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restSponsoring: RestSponsoring): ISponsoring {
    return {
      ...restSponsoring,
      startDate: restSponsoring.startDate ? dayjs(restSponsoring.startDate) : undefined,
      endDate: restSponsoring.endDate ? dayjs(restSponsoring.endDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSponsoring>): HttpResponse<ISponsoring> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSponsoring[]>): HttpResponse<ISponsoring[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
