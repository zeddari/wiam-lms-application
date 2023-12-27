import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICountry2, NewCountry2 } from '../country-2.model';

export type PartialUpdateCountry2 = Partial<ICountry2> & Pick<ICountry2, 'id'>;

export type EntityResponseType = HttpResponse<ICountry2>;
export type EntityArrayResponseType = HttpResponse<ICountry2[]>;

@Injectable({ providedIn: 'root' })
export class Country2Service {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/country-2-s');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/country-2-s/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(country2: NewCountry2): Observable<EntityResponseType> {
    return this.http.post<ICountry2>(this.resourceUrl, country2, { observe: 'response' });
  }

  update(country2: ICountry2): Observable<EntityResponseType> {
    return this.http.put<ICountry2>(`${this.resourceUrl}/${this.getCountry2Identifier(country2)}`, country2, { observe: 'response' });
  }

  partialUpdate(country2: PartialUpdateCountry2): Observable<EntityResponseType> {
    return this.http.patch<ICountry2>(`${this.resourceUrl}/${this.getCountry2Identifier(country2)}`, country2, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICountry2>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICountry2[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICountry2[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ICountry2[]>()], asapScheduler)));
  }

  getCountry2Identifier(country2: Pick<ICountry2, 'id'>): number {
    return country2.id;
  }

  compareCountry2(o1: Pick<ICountry2, 'id'> | null, o2: Pick<ICountry2, 'id'> | null): boolean {
    return o1 && o2 ? this.getCountry2Identifier(o1) === this.getCountry2Identifier(o2) : o1 === o2;
  }

  addCountry2ToCollectionIfMissing<Type extends Pick<ICountry2, 'id'>>(
    country2Collection: Type[],
    ...country2sToCheck: (Type | null | undefined)[]
  ): Type[] {
    const country2s: Type[] = country2sToCheck.filter(isPresent);
    if (country2s.length > 0) {
      const country2CollectionIdentifiers = country2Collection.map(country2Item => this.getCountry2Identifier(country2Item)!);
      const country2sToAdd = country2s.filter(country2Item => {
        const country2Identifier = this.getCountry2Identifier(country2Item);
        if (country2CollectionIdentifiers.includes(country2Identifier)) {
          return false;
        }
        country2CollectionIdentifiers.push(country2Identifier);
        return true;
      });
      return [...country2sToAdd, ...country2Collection];
    }
    return country2Collection;
  }
}
