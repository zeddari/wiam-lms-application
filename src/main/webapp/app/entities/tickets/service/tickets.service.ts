import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ITickets, NewTickets } from '../tickets.model';

export type PartialUpdateTickets = Partial<ITickets> & Pick<ITickets, 'id'>;

type RestOf<T extends ITickets | NewTickets> = Omit<T, 'dateTicket' | 'dateProcess'> & {
  dateTicket?: string | null;
  dateProcess?: string | null;
};

export type RestTickets = RestOf<ITickets>;

export type NewRestTickets = RestOf<NewTickets>;

export type PartialUpdateRestTickets = RestOf<PartialUpdateTickets>;

export type EntityResponseType = HttpResponse<ITickets>;
export type EntityArrayResponseType = HttpResponse<ITickets[]>;

@Injectable({ providedIn: 'root' })
export class TicketsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tickets');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/tickets/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(tickets: NewTickets): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tickets);
    return this.http
      .post<RestTickets>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(tickets: ITickets): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tickets);
    return this.http
      .put<RestTickets>(`${this.resourceUrl}/${this.getTicketsIdentifier(tickets)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(tickets: PartialUpdateTickets): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(tickets);
    return this.http
      .patch<RestTickets>(`${this.resourceUrl}/${this.getTicketsIdentifier(tickets)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestTickets>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestTickets[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestTickets[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<ITickets[]>()], asapScheduler)),
    );
  }

  getTicketsIdentifier(tickets: Pick<ITickets, 'id'>): number {
    return tickets.id;
  }

  compareTickets(o1: Pick<ITickets, 'id'> | null, o2: Pick<ITickets, 'id'> | null): boolean {
    return o1 && o2 ? this.getTicketsIdentifier(o1) === this.getTicketsIdentifier(o2) : o1 === o2;
  }

  addTicketsToCollectionIfMissing<Type extends Pick<ITickets, 'id'>>(
    ticketsCollection: Type[],
    ...ticketsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tickets: Type[] = ticketsToCheck.filter(isPresent);
    if (tickets.length > 0) {
      const ticketsCollectionIdentifiers = ticketsCollection.map(ticketsItem => this.getTicketsIdentifier(ticketsItem)!);
      const ticketsToAdd = tickets.filter(ticketsItem => {
        const ticketsIdentifier = this.getTicketsIdentifier(ticketsItem);
        if (ticketsCollectionIdentifiers.includes(ticketsIdentifier)) {
          return false;
        }
        ticketsCollectionIdentifiers.push(ticketsIdentifier);
        return true;
      });
      return [...ticketsToAdd, ...ticketsCollection];
    }
    return ticketsCollection;
  }

  protected convertDateFromClient<T extends ITickets | NewTickets | PartialUpdateTickets>(tickets: T): RestOf<T> {
    return {
      ...tickets,
      dateTicket: tickets.dateTicket?.toJSON() ?? null,
      dateProcess: tickets.dateProcess?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restTickets: RestTickets): ITickets {
    return {
      ...restTickets,
      dateTicket: restTickets.dateTicket ? dayjs(restTickets.dateTicket) : undefined,
      dateProcess: restTickets.dateProcess ? dayjs(restTickets.dateProcess) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestTickets>): HttpResponse<ITickets> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestTickets[]>): HttpResponse<ITickets[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
