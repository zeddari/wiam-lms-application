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
import { IProgression, NewProgression } from '../progression.model';

export type PartialUpdateProgression = Partial<IProgression> & Pick<IProgression, 'id'>;

type RestOf<T extends IProgression | NewProgression> = Omit<T, 'progressionDate'> & {
  progressionDate?: string | null;
};

export type RestProgression = RestOf<IProgression>;

export type NewRestProgression = RestOf<NewProgression>;

export type PartialUpdateRestProgression = RestOf<PartialUpdateProgression>;

export type EntityResponseType = HttpResponse<IProgression>;
export type EntityArrayResponseType = HttpResponse<IProgression[]>;

@Injectable({ providedIn: 'root' })
export class ProgressionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/progressions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/progressions/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(progression: NewProgression): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(progression);
    return this.http
      .post<RestProgression>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(progression: IProgression): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(progression);
    return this.http
      .put<RestProgression>(`${this.resourceUrl}/${this.getProgressionIdentifier(progression)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(progression: PartialUpdateProgression): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(progression);
    return this.http
      .patch<RestProgression>(`${this.resourceUrl}/${this.getProgressionIdentifier(progression)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProgression>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProgression[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestProgression[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<IProgression[]>()], asapScheduler)),
    );
  }

  getProgressionIdentifier(progression: Pick<IProgression, 'id'>): number {
    return progression.id;
  }

  compareProgression(o1: Pick<IProgression, 'id'> | null, o2: Pick<IProgression, 'id'> | null): boolean {
    return o1 && o2 ? this.getProgressionIdentifier(o1) === this.getProgressionIdentifier(o2) : o1 === o2;
  }

  addProgressionToCollectionIfMissing<Type extends Pick<IProgression, 'id'>>(
    progressionCollection: Type[],
    ...progressionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const progressions: Type[] = progressionsToCheck.filter(isPresent);
    if (progressions.length > 0) {
      const progressionCollectionIdentifiers = progressionCollection.map(
        progressionItem => this.getProgressionIdentifier(progressionItem)!,
      );
      const progressionsToAdd = progressions.filter(progressionItem => {
        const progressionIdentifier = this.getProgressionIdentifier(progressionItem);
        if (progressionCollectionIdentifiers.includes(progressionIdentifier)) {
          return false;
        }
        progressionCollectionIdentifiers.push(progressionIdentifier);
        return true;
      });
      return [...progressionsToAdd, ...progressionCollection];
    }
    return progressionCollection;
  }

  protected convertDateFromClient<T extends IProgression | NewProgression | PartialUpdateProgression>(progression: T): RestOf<T> {
    return {
      ...progression,
      progressionDate: progression.progressionDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restProgression: RestProgression): IProgression {
    return {
      ...restProgression,
      progressionDate: restProgression.progressionDate ? dayjs(restProgression.progressionDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProgression>): HttpResponse<IProgression> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProgression[]>): HttpResponse<IProgression[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
