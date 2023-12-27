import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ISessionLink, NewSessionLink } from '../session-link.model';

export type PartialUpdateSessionLink = Partial<ISessionLink> & Pick<ISessionLink, 'id'>;

export type EntityResponseType = HttpResponse<ISessionLink>;
export type EntityArrayResponseType = HttpResponse<ISessionLink[]>;

@Injectable({ providedIn: 'root' })
export class SessionLinkService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/session-links');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/session-links/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(sessionLink: NewSessionLink): Observable<EntityResponseType> {
    return this.http.post<ISessionLink>(this.resourceUrl, sessionLink, { observe: 'response' });
  }

  update(sessionLink: ISessionLink): Observable<EntityResponseType> {
    return this.http.put<ISessionLink>(`${this.resourceUrl}/${this.getSessionLinkIdentifier(sessionLink)}`, sessionLink, {
      observe: 'response',
    });
  }

  partialUpdate(sessionLink: PartialUpdateSessionLink): Observable<EntityResponseType> {
    return this.http.patch<ISessionLink>(`${this.resourceUrl}/${this.getSessionLinkIdentifier(sessionLink)}`, sessionLink, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISessionLink>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISessionLink[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISessionLink[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ISessionLink[]>()], asapScheduler)));
  }

  getSessionLinkIdentifier(sessionLink: Pick<ISessionLink, 'id'>): number {
    return sessionLink.id;
  }

  compareSessionLink(o1: Pick<ISessionLink, 'id'> | null, o2: Pick<ISessionLink, 'id'> | null): boolean {
    return o1 && o2 ? this.getSessionLinkIdentifier(o1) === this.getSessionLinkIdentifier(o2) : o1 === o2;
  }

  addSessionLinkToCollectionIfMissing<Type extends Pick<ISessionLink, 'id'>>(
    sessionLinkCollection: Type[],
    ...sessionLinksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sessionLinks: Type[] = sessionLinksToCheck.filter(isPresent);
    if (sessionLinks.length > 0) {
      const sessionLinkCollectionIdentifiers = sessionLinkCollection.map(
        sessionLinkItem => this.getSessionLinkIdentifier(sessionLinkItem)!,
      );
      const sessionLinksToAdd = sessionLinks.filter(sessionLinkItem => {
        const sessionLinkIdentifier = this.getSessionLinkIdentifier(sessionLinkItem);
        if (sessionLinkCollectionIdentifiers.includes(sessionLinkIdentifier)) {
          return false;
        }
        sessionLinkCollectionIdentifiers.push(sessionLinkIdentifier);
        return true;
      });
      return [...sessionLinksToAdd, ...sessionLinkCollection];
    }
    return sessionLinkCollection;
  }
}
