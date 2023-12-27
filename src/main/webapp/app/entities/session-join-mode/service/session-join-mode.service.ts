import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ISessionJoinMode, NewSessionJoinMode } from '../session-join-mode.model';

export type PartialUpdateSessionJoinMode = Partial<ISessionJoinMode> & Pick<ISessionJoinMode, 'id'>;

export type EntityResponseType = HttpResponse<ISessionJoinMode>;
export type EntityArrayResponseType = HttpResponse<ISessionJoinMode[]>;

@Injectable({ providedIn: 'root' })
export class SessionJoinModeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/session-join-modes');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/session-join-modes/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(sessionJoinMode: NewSessionJoinMode): Observable<EntityResponseType> {
    return this.http.post<ISessionJoinMode>(this.resourceUrl, sessionJoinMode, { observe: 'response' });
  }

  update(sessionJoinMode: ISessionJoinMode): Observable<EntityResponseType> {
    return this.http.put<ISessionJoinMode>(`${this.resourceUrl}/${this.getSessionJoinModeIdentifier(sessionJoinMode)}`, sessionJoinMode, {
      observe: 'response',
    });
  }

  partialUpdate(sessionJoinMode: PartialUpdateSessionJoinMode): Observable<EntityResponseType> {
    return this.http.patch<ISessionJoinMode>(`${this.resourceUrl}/${this.getSessionJoinModeIdentifier(sessionJoinMode)}`, sessionJoinMode, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISessionJoinMode>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISessionJoinMode[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISessionJoinMode[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ISessionJoinMode[]>()], asapScheduler)));
  }

  getSessionJoinModeIdentifier(sessionJoinMode: Pick<ISessionJoinMode, 'id'>): number {
    return sessionJoinMode.id;
  }

  compareSessionJoinMode(o1: Pick<ISessionJoinMode, 'id'> | null, o2: Pick<ISessionJoinMode, 'id'> | null): boolean {
    return o1 && o2 ? this.getSessionJoinModeIdentifier(o1) === this.getSessionJoinModeIdentifier(o2) : o1 === o2;
  }

  addSessionJoinModeToCollectionIfMissing<Type extends Pick<ISessionJoinMode, 'id'>>(
    sessionJoinModeCollection: Type[],
    ...sessionJoinModesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sessionJoinModes: Type[] = sessionJoinModesToCheck.filter(isPresent);
    if (sessionJoinModes.length > 0) {
      const sessionJoinModeCollectionIdentifiers = sessionJoinModeCollection.map(
        sessionJoinModeItem => this.getSessionJoinModeIdentifier(sessionJoinModeItem)!,
      );
      const sessionJoinModesToAdd = sessionJoinModes.filter(sessionJoinModeItem => {
        const sessionJoinModeIdentifier = this.getSessionJoinModeIdentifier(sessionJoinModeItem);
        if (sessionJoinModeCollectionIdentifiers.includes(sessionJoinModeIdentifier)) {
          return false;
        }
        sessionJoinModeCollectionIdentifiers.push(sessionJoinModeIdentifier);
        return true;
      });
      return [...sessionJoinModesToAdd, ...sessionJoinModeCollection];
    }
    return sessionJoinModeCollection;
  }
}
