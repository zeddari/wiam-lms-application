import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ILeaveRequest, NewLeaveRequest } from '../leave-request.model';

export type PartialUpdateLeaveRequest = Partial<ILeaveRequest> & Pick<ILeaveRequest, 'id'>;

type RestOf<T extends ILeaveRequest | NewLeaveRequest> = Omit<T, 'from' | 'toDate'> & {
  from?: string | null;
  toDate?: string | null;
};

export type RestLeaveRequest = RestOf<ILeaveRequest>;

export type NewRestLeaveRequest = RestOf<NewLeaveRequest>;

export type PartialUpdateRestLeaveRequest = RestOf<PartialUpdateLeaveRequest>;

export type EntityResponseType = HttpResponse<ILeaveRequest>;
export type EntityArrayResponseType = HttpResponse<ILeaveRequest[]>;

@Injectable({ providedIn: 'root' })
export class LeaveRequestService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/leave-requests');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/leave-requests/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(leaveRequest: NewLeaveRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(leaveRequest);
    return this.http
      .post<RestLeaveRequest>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(leaveRequest: ILeaveRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(leaveRequest);
    return this.http
      .put<RestLeaveRequest>(`${this.resourceUrl}/${this.getLeaveRequestIdentifier(leaveRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(leaveRequest: PartialUpdateLeaveRequest): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(leaveRequest);
    return this.http
      .patch<RestLeaveRequest>(`${this.resourceUrl}/${this.getLeaveRequestIdentifier(leaveRequest)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestLeaveRequest>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLeaveRequest[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestLeaveRequest[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<ILeaveRequest[]>()], asapScheduler)),
    );
  }

  getLeaveRequestIdentifier(leaveRequest: Pick<ILeaveRequest, 'id'>): number {
    return leaveRequest.id;
  }

  compareLeaveRequest(o1: Pick<ILeaveRequest, 'id'> | null, o2: Pick<ILeaveRequest, 'id'> | null): boolean {
    return o1 && o2 ? this.getLeaveRequestIdentifier(o1) === this.getLeaveRequestIdentifier(o2) : o1 === o2;
  }

  addLeaveRequestToCollectionIfMissing<Type extends Pick<ILeaveRequest, 'id'>>(
    leaveRequestCollection: Type[],
    ...leaveRequestsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const leaveRequests: Type[] = leaveRequestsToCheck.filter(isPresent);
    if (leaveRequests.length > 0) {
      const leaveRequestCollectionIdentifiers = leaveRequestCollection.map(
        leaveRequestItem => this.getLeaveRequestIdentifier(leaveRequestItem)!,
      );
      const leaveRequestsToAdd = leaveRequests.filter(leaveRequestItem => {
        const leaveRequestIdentifier = this.getLeaveRequestIdentifier(leaveRequestItem);
        if (leaveRequestCollectionIdentifiers.includes(leaveRequestIdentifier)) {
          return false;
        }
        leaveRequestCollectionIdentifiers.push(leaveRequestIdentifier);
        return true;
      });
      return [...leaveRequestsToAdd, ...leaveRequestCollection];
    }
    return leaveRequestCollection;
  }

  protected convertDateFromClient<T extends ILeaveRequest | NewLeaveRequest | PartialUpdateLeaveRequest>(leaveRequest: T): RestOf<T> {
    return {
      ...leaveRequest,
      from: leaveRequest.from?.toJSON() ?? null,
      toDate: leaveRequest.toDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restLeaveRequest: RestLeaveRequest): ILeaveRequest {
    return {
      ...restLeaveRequest,
      from: restLeaveRequest.from ? dayjs(restLeaveRequest.from) : undefined,
      toDate: restLeaveRequest.toDate ? dayjs(restLeaveRequest.toDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestLeaveRequest>): HttpResponse<ILeaveRequest> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestLeaveRequest[]>): HttpResponse<ILeaveRequest[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
