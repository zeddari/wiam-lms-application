import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IProgressionMode, NewProgressionMode } from '../progression-mode.model';

export type PartialUpdateProgressionMode = Partial<IProgressionMode> & Pick<IProgressionMode, 'id'>;

export type EntityResponseType = HttpResponse<IProgressionMode>;
export type EntityArrayResponseType = HttpResponse<IProgressionMode[]>;

@Injectable({ providedIn: 'root' })
export class ProgressionModeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/progression-modes');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/progression-modes/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(progressionMode: NewProgressionMode): Observable<EntityResponseType> {
    return this.http.post<IProgressionMode>(this.resourceUrl, progressionMode, { observe: 'response' });
  }

  update(progressionMode: IProgressionMode): Observable<EntityResponseType> {
    return this.http.put<IProgressionMode>(`${this.resourceUrl}/${this.getProgressionModeIdentifier(progressionMode)}`, progressionMode, {
      observe: 'response',
    });
  }

  partialUpdate(progressionMode: PartialUpdateProgressionMode): Observable<EntityResponseType> {
    return this.http.patch<IProgressionMode>(`${this.resourceUrl}/${this.getProgressionModeIdentifier(progressionMode)}`, progressionMode, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProgressionMode>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProgressionMode[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProgressionMode[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IProgressionMode[]>()], asapScheduler)));
  }

  getProgressionModeIdentifier(progressionMode: Pick<IProgressionMode, 'id'>): number {
    return progressionMode.id;
  }

  compareProgressionMode(o1: Pick<IProgressionMode, 'id'> | null, o2: Pick<IProgressionMode, 'id'> | null): boolean {
    return o1 && o2 ? this.getProgressionModeIdentifier(o1) === this.getProgressionModeIdentifier(o2) : o1 === o2;
  }

  addProgressionModeToCollectionIfMissing<Type extends Pick<IProgressionMode, 'id'>>(
    progressionModeCollection: Type[],
    ...progressionModesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const progressionModes: Type[] = progressionModesToCheck.filter(isPresent);
    if (progressionModes.length > 0) {
      const progressionModeCollectionIdentifiers = progressionModeCollection.map(
        progressionModeItem => this.getProgressionModeIdentifier(progressionModeItem)!,
      );
      const progressionModesToAdd = progressionModes.filter(progressionModeItem => {
        const progressionModeIdentifier = this.getProgressionModeIdentifier(progressionModeItem);
        if (progressionModeCollectionIdentifiers.includes(progressionModeIdentifier)) {
          return false;
        }
        progressionModeCollectionIdentifiers.push(progressionModeIdentifier);
        return true;
      });
      return [...progressionModesToAdd, ...progressionModeCollection];
    }
    return progressionModeCollection;
  }
}
