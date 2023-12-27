import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IDepartement, NewDepartement } from '../departement.model';

export type PartialUpdateDepartement = Partial<IDepartement> & Pick<IDepartement, 'id'>;

export type EntityResponseType = HttpResponse<IDepartement>;
export type EntityArrayResponseType = HttpResponse<IDepartement[]>;

@Injectable({ providedIn: 'root' })
export class DepartementService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/departements');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/departements/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(departement: NewDepartement): Observable<EntityResponseType> {
    return this.http.post<IDepartement>(this.resourceUrl, departement, { observe: 'response' });
  }

  update(departement: IDepartement): Observable<EntityResponseType> {
    return this.http.put<IDepartement>(`${this.resourceUrl}/${this.getDepartementIdentifier(departement)}`, departement, {
      observe: 'response',
    });
  }

  partialUpdate(departement: PartialUpdateDepartement): Observable<EntityResponseType> {
    return this.http.patch<IDepartement>(`${this.resourceUrl}/${this.getDepartementIdentifier(departement)}`, departement, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDepartement>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDepartement[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDepartement[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IDepartement[]>()], asapScheduler)));
  }

  getDepartementIdentifier(departement: Pick<IDepartement, 'id'>): number {
    return departement.id;
  }

  compareDepartement(o1: Pick<IDepartement, 'id'> | null, o2: Pick<IDepartement, 'id'> | null): boolean {
    return o1 && o2 ? this.getDepartementIdentifier(o1) === this.getDepartementIdentifier(o2) : o1 === o2;
  }

  addDepartementToCollectionIfMissing<Type extends Pick<IDepartement, 'id'>>(
    departementCollection: Type[],
    ...departementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const departements: Type[] = departementsToCheck.filter(isPresent);
    if (departements.length > 0) {
      const departementCollectionIdentifiers = departementCollection.map(
        departementItem => this.getDepartementIdentifier(departementItem)!,
      );
      const departementsToAdd = departements.filter(departementItem => {
        const departementIdentifier = this.getDepartementIdentifier(departementItem);
        if (departementCollectionIdentifiers.includes(departementIdentifier)) {
          return false;
        }
        departementCollectionIdentifiers.push(departementIdentifier);
        return true;
      });
      return [...departementsToAdd, ...departementCollection];
    }
    return departementCollection;
  }
}
