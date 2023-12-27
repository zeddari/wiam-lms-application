import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IFollowUp, NewFollowUp } from '../follow-up.model';

export type PartialUpdateFollowUp = Partial<IFollowUp> & Pick<IFollowUp, 'id'>;

export type EntityResponseType = HttpResponse<IFollowUp>;
export type EntityArrayResponseType = HttpResponse<IFollowUp[]>;

@Injectable({ providedIn: 'root' })
export class FollowUpService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/follow-ups');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/follow-ups/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(followUp: NewFollowUp): Observable<EntityResponseType> {
    return this.http.post<IFollowUp>(this.resourceUrl, followUp, { observe: 'response' });
  }

  update(followUp: IFollowUp): Observable<EntityResponseType> {
    return this.http.put<IFollowUp>(`${this.resourceUrl}/${this.getFollowUpIdentifier(followUp)}`, followUp, { observe: 'response' });
  }

  partialUpdate(followUp: PartialUpdateFollowUp): Observable<EntityResponseType> {
    return this.http.patch<IFollowUp>(`${this.resourceUrl}/${this.getFollowUpIdentifier(followUp)}`, followUp, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFollowUp>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFollowUp[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IFollowUp[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IFollowUp[]>()], asapScheduler)));
  }

  getFollowUpIdentifier(followUp: Pick<IFollowUp, 'id'>): number {
    return followUp.id;
  }

  compareFollowUp(o1: Pick<IFollowUp, 'id'> | null, o2: Pick<IFollowUp, 'id'> | null): boolean {
    return o1 && o2 ? this.getFollowUpIdentifier(o1) === this.getFollowUpIdentifier(o2) : o1 === o2;
  }

  addFollowUpToCollectionIfMissing<Type extends Pick<IFollowUp, 'id'>>(
    followUpCollection: Type[],
    ...followUpsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const followUps: Type[] = followUpsToCheck.filter(isPresent);
    if (followUps.length > 0) {
      const followUpCollectionIdentifiers = followUpCollection.map(followUpItem => this.getFollowUpIdentifier(followUpItem)!);
      const followUpsToAdd = followUps.filter(followUpItem => {
        const followUpIdentifier = this.getFollowUpIdentifier(followUpItem);
        if (followUpCollectionIdentifiers.includes(followUpIdentifier)) {
          return false;
        }
        followUpCollectionIdentifiers.push(followUpIdentifier);
        return true;
      });
      return [...followUpsToAdd, ...followUpCollection];
    }
    return followUpCollection;
  }
}
