import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ISessionProvider, NewSessionProvider } from '../session-provider.model';

export type PartialUpdateSessionProvider = Partial<ISessionProvider> & Pick<ISessionProvider, 'id'>;

export type EntityResponseType = HttpResponse<ISessionProvider>;
export type EntityArrayResponseType = HttpResponse<ISessionProvider[]>;

@Injectable({ providedIn: 'root' })
export class SessionProviderService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/session-providers');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/session-providers/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(sessionProvider: NewSessionProvider): Observable<EntityResponseType> {
    return this.http.post<ISessionProvider>(this.resourceUrl, sessionProvider, { observe: 'response' });
  }

  update(sessionProvider: ISessionProvider): Observable<EntityResponseType> {
    return this.http.put<ISessionProvider>(`${this.resourceUrl}/${this.getSessionProviderIdentifier(sessionProvider)}`, sessionProvider, {
      observe: 'response',
    });
  }

  partialUpdate(sessionProvider: PartialUpdateSessionProvider): Observable<EntityResponseType> {
    return this.http.patch<ISessionProvider>(`${this.resourceUrl}/${this.getSessionProviderIdentifier(sessionProvider)}`, sessionProvider, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISessionProvider>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISessionProvider[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISessionProvider[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ISessionProvider[]>()], asapScheduler)));
  }

  getSessionProviderIdentifier(sessionProvider: Pick<ISessionProvider, 'id'>): number {
    return sessionProvider.id;
  }

  compareSessionProvider(o1: Pick<ISessionProvider, 'id'> | null, o2: Pick<ISessionProvider, 'id'> | null): boolean {
    return o1 && o2 ? this.getSessionProviderIdentifier(o1) === this.getSessionProviderIdentifier(o2) : o1 === o2;
  }

  addSessionProviderToCollectionIfMissing<Type extends Pick<ISessionProvider, 'id'>>(
    sessionProviderCollection: Type[],
    ...sessionProvidersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sessionProviders: Type[] = sessionProvidersToCheck.filter(isPresent);
    if (sessionProviders.length > 0) {
      const sessionProviderCollectionIdentifiers = sessionProviderCollection.map(
        sessionProviderItem => this.getSessionProviderIdentifier(sessionProviderItem)!,
      );
      const sessionProvidersToAdd = sessionProviders.filter(sessionProviderItem => {
        const sessionProviderIdentifier = this.getSessionProviderIdentifier(sessionProviderItem);
        if (sessionProviderCollectionIdentifiers.includes(sessionProviderIdentifier)) {
          return false;
        }
        sessionProviderCollectionIdentifiers.push(sessionProviderIdentifier);
        return true;
      });
      return [...sessionProvidersToAdd, ...sessionProviderCollection];
    }
    return sessionProviderCollection;
  }
}
