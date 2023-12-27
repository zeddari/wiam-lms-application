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
import { ISponsor, NewSponsor } from '../sponsor.model';

export type PartialUpdateSponsor = Partial<ISponsor> & Pick<ISponsor, 'id'>;

type RestOf<T extends ISponsor | NewSponsor> = Omit<T, 'birthdate'> & {
  birthdate?: string | null;
};

export type RestSponsor = RestOf<ISponsor>;

export type NewRestSponsor = RestOf<NewSponsor>;

export type PartialUpdateRestSponsor = RestOf<PartialUpdateSponsor>;

export type EntityResponseType = HttpResponse<ISponsor>;
export type EntityArrayResponseType = HttpResponse<ISponsor[]>;

@Injectable({ providedIn: 'root' })
export class SponsorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sponsors');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/sponsors/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(sponsor: NewSponsor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sponsor);
    return this.http
      .post<RestSponsor>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(sponsor: ISponsor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sponsor);
    return this.http
      .put<RestSponsor>(`${this.resourceUrl}/${this.getSponsorIdentifier(sponsor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(sponsor: PartialUpdateSponsor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(sponsor);
    return this.http
      .patch<RestSponsor>(`${this.resourceUrl}/${this.getSponsorIdentifier(sponsor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSponsor>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSponsor[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestSponsor[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<ISponsor[]>()], asapScheduler)),
    );
  }

  getSponsorIdentifier(sponsor: Pick<ISponsor, 'id'>): number {
    return sponsor.id;
  }

  compareSponsor(o1: Pick<ISponsor, 'id'> | null, o2: Pick<ISponsor, 'id'> | null): boolean {
    return o1 && o2 ? this.getSponsorIdentifier(o1) === this.getSponsorIdentifier(o2) : o1 === o2;
  }

  addSponsorToCollectionIfMissing<Type extends Pick<ISponsor, 'id'>>(
    sponsorCollection: Type[],
    ...sponsorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sponsors: Type[] = sponsorsToCheck.filter(isPresent);
    if (sponsors.length > 0) {
      const sponsorCollectionIdentifiers = sponsorCollection.map(sponsorItem => this.getSponsorIdentifier(sponsorItem)!);
      const sponsorsToAdd = sponsors.filter(sponsorItem => {
        const sponsorIdentifier = this.getSponsorIdentifier(sponsorItem);
        if (sponsorCollectionIdentifiers.includes(sponsorIdentifier)) {
          return false;
        }
        sponsorCollectionIdentifiers.push(sponsorIdentifier);
        return true;
      });
      return [...sponsorsToAdd, ...sponsorCollection];
    }
    return sponsorCollection;
  }

  protected convertDateFromClient<T extends ISponsor | NewSponsor | PartialUpdateSponsor>(sponsor: T): RestOf<T> {
    return {
      ...sponsor,
      birthdate: sponsor.birthdate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restSponsor: RestSponsor): ISponsor {
    return {
      ...restSponsor,
      birthdate: restSponsor.birthdate ? dayjs(restSponsor.birthdate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSponsor>): HttpResponse<ISponsor> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSponsor[]>): HttpResponse<ISponsor[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
