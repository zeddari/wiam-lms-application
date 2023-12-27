import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ITicketSubjects, NewTicketSubjects } from '../ticket-subjects.model';

export type PartialUpdateTicketSubjects = Partial<ITicketSubjects> & Pick<ITicketSubjects, 'id'>;

export type EntityResponseType = HttpResponse<ITicketSubjects>;
export type EntityArrayResponseType = HttpResponse<ITicketSubjects[]>;

@Injectable({ providedIn: 'root' })
export class TicketSubjectsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/ticket-subjects');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/ticket-subjects/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(ticketSubjects: NewTicketSubjects): Observable<EntityResponseType> {
    return this.http.post<ITicketSubjects>(this.resourceUrl, ticketSubjects, { observe: 'response' });
  }

  update(ticketSubjects: ITicketSubjects): Observable<EntityResponseType> {
    return this.http.put<ITicketSubjects>(`${this.resourceUrl}/${this.getTicketSubjectsIdentifier(ticketSubjects)}`, ticketSubjects, {
      observe: 'response',
    });
  }

  partialUpdate(ticketSubjects: PartialUpdateTicketSubjects): Observable<EntityResponseType> {
    return this.http.patch<ITicketSubjects>(`${this.resourceUrl}/${this.getTicketSubjectsIdentifier(ticketSubjects)}`, ticketSubjects, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITicketSubjects>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITicketSubjects[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITicketSubjects[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ITicketSubjects[]>()], asapScheduler)));
  }

  getTicketSubjectsIdentifier(ticketSubjects: Pick<ITicketSubjects, 'id'>): number {
    return ticketSubjects.id;
  }

  compareTicketSubjects(o1: Pick<ITicketSubjects, 'id'> | null, o2: Pick<ITicketSubjects, 'id'> | null): boolean {
    return o1 && o2 ? this.getTicketSubjectsIdentifier(o1) === this.getTicketSubjectsIdentifier(o2) : o1 === o2;
  }

  addTicketSubjectsToCollectionIfMissing<Type extends Pick<ITicketSubjects, 'id'>>(
    ticketSubjectsCollection: Type[],
    ...ticketSubjectsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ticketSubjects: Type[] = ticketSubjectsToCheck.filter(isPresent);
    if (ticketSubjects.length > 0) {
      const ticketSubjectsCollectionIdentifiers = ticketSubjectsCollection.map(
        ticketSubjectsItem => this.getTicketSubjectsIdentifier(ticketSubjectsItem)!,
      );
      const ticketSubjectsToAdd = ticketSubjects.filter(ticketSubjectsItem => {
        const ticketSubjectsIdentifier = this.getTicketSubjectsIdentifier(ticketSubjectsItem);
        if (ticketSubjectsCollectionIdentifiers.includes(ticketSubjectsIdentifier)) {
          return false;
        }
        ticketSubjectsCollectionIdentifiers.push(ticketSubjectsIdentifier);
        return true;
      });
      return [...ticketSubjectsToAdd, ...ticketSubjectsCollection];
    }
    return ticketSubjectsCollection;
  }
}
