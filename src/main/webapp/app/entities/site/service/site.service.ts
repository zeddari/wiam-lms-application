import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ISite, NewSite } from '../site.model';

export type PartialUpdateSite = Partial<ISite> & Pick<ISite, 'id'>;

export type EntityResponseType = HttpResponse<ISite>;
export type EntityArrayResponseType = HttpResponse<ISite[]>;

@Injectable({ providedIn: 'root' })
export class SiteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/sites');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/sites/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(site: NewSite): Observable<EntityResponseType> {
    return this.http.post<ISite>(this.resourceUrl, site, { observe: 'response' });
  }

  update(site: ISite): Observable<EntityResponseType> {
    return this.http.put<ISite>(`${this.resourceUrl}/${this.getSiteIdentifier(site)}`, site, { observe: 'response' });
  }

  partialUpdate(site: PartialUpdateSite): Observable<EntityResponseType> {
    return this.http.patch<ISite>(`${this.resourceUrl}/${this.getSiteIdentifier(site)}`, site, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISite>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISite[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISite[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ISite[]>()], asapScheduler)));
  }

  getSiteIdentifier(site: Pick<ISite, 'id'>): number {
    return site.id;
  }

  compareSite(o1: Pick<ISite, 'id'> | null, o2: Pick<ISite, 'id'> | null): boolean {
    return o1 && o2 ? this.getSiteIdentifier(o1) === this.getSiteIdentifier(o2) : o1 === o2;
  }

  addSiteToCollectionIfMissing<Type extends Pick<ISite, 'id'>>(
    siteCollection: Type[],
    ...sitesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sites: Type[] = sitesToCheck.filter(isPresent);
    if (sites.length > 0) {
      const siteCollectionIdentifiers = siteCollection.map(siteItem => this.getSiteIdentifier(siteItem)!);
      const sitesToAdd = sites.filter(siteItem => {
        const siteIdentifier = this.getSiteIdentifier(siteItem);
        if (siteCollectionIdentifiers.includes(siteIdentifier)) {
          return false;
        }
        siteCollectionIdentifiers.push(siteIdentifier);
        return true;
      });
      return [...sitesToAdd, ...siteCollection];
    }
    return siteCollection;
  }
}
