import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ISessionMode, NewSessionMode } from '../session-mode.model';

export type PartialUpdateSessionMode = Partial<ISessionMode> & Pick<ISessionMode, 'id'>;

export type EntityResponseType = HttpResponse<ISessionMode>;
export type EntityArrayResponseType = HttpResponse<ISessionMode[]>;

@Injectable({ providedIn: 'root' })
export class SessionModeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/session-modes');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/session-modes/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(sessionMode: NewSessionMode): Observable<EntityResponseType> {
    return this.http.post<ISessionMode>(this.resourceUrl, sessionMode, { observe: 'response' });
  }

  update(sessionMode: ISessionMode): Observable<EntityResponseType> {
    return this.http.put<ISessionMode>(`${this.resourceUrl}/${this.getSessionModeIdentifier(sessionMode)}`, sessionMode, {
      observe: 'response',
    });
  }

  partialUpdate(sessionMode: PartialUpdateSessionMode): Observable<EntityResponseType> {
    return this.http.patch<ISessionMode>(`${this.resourceUrl}/${this.getSessionModeIdentifier(sessionMode)}`, sessionMode, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISessionMode>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISessionMode[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISessionMode[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ISessionMode[]>()], asapScheduler)));
  }

  getSessionModeIdentifier(sessionMode: Pick<ISessionMode, 'id'>): number {
    return sessionMode.id;
  }

  compareSessionMode(o1: Pick<ISessionMode, 'id'> | null, o2: Pick<ISessionMode, 'id'> | null): boolean {
    return o1 && o2 ? this.getSessionModeIdentifier(o1) === this.getSessionModeIdentifier(o2) : o1 === o2;
  }

  addSessionModeToCollectionIfMissing<Type extends Pick<ISessionMode, 'id'>>(
    sessionModeCollection: Type[],
    ...sessionModesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const sessionModes: Type[] = sessionModesToCheck.filter(isPresent);
    if (sessionModes.length > 0) {
      const sessionModeCollectionIdentifiers = sessionModeCollection.map(
        sessionModeItem => this.getSessionModeIdentifier(sessionModeItem)!,
      );
      const sessionModesToAdd = sessionModes.filter(sessionModeItem => {
        const sessionModeIdentifier = this.getSessionModeIdentifier(sessionModeItem);
        if (sessionModeCollectionIdentifiers.includes(sessionModeIdentifier)) {
          return false;
        }
        sessionModeCollectionIdentifiers.push(sessionModeIdentifier);
        return true;
      });
      return [...sessionModesToAdd, ...sessionModeCollection];
    }
    return sessionModeCollection;
  }
}
