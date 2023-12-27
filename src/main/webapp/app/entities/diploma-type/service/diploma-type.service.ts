import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IDiplomaType, NewDiplomaType } from '../diploma-type.model';

export type PartialUpdateDiplomaType = Partial<IDiplomaType> & Pick<IDiplomaType, 'id'>;

export type EntityResponseType = HttpResponse<IDiplomaType>;
export type EntityArrayResponseType = HttpResponse<IDiplomaType[]>;

@Injectable({ providedIn: 'root' })
export class DiplomaTypeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/diploma-types');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/diploma-types/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(diplomaType: NewDiplomaType): Observable<EntityResponseType> {
    return this.http.post<IDiplomaType>(this.resourceUrl, diplomaType, { observe: 'response' });
  }

  update(diplomaType: IDiplomaType): Observable<EntityResponseType> {
    return this.http.put<IDiplomaType>(`${this.resourceUrl}/${this.getDiplomaTypeIdentifier(diplomaType)}`, diplomaType, {
      observe: 'response',
    });
  }

  partialUpdate(diplomaType: PartialUpdateDiplomaType): Observable<EntityResponseType> {
    return this.http.patch<IDiplomaType>(`${this.resourceUrl}/${this.getDiplomaTypeIdentifier(diplomaType)}`, diplomaType, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDiplomaType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDiplomaType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDiplomaType[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IDiplomaType[]>()], asapScheduler)));
  }

  getDiplomaTypeIdentifier(diplomaType: Pick<IDiplomaType, 'id'>): number {
    return diplomaType.id;
  }

  compareDiplomaType(o1: Pick<IDiplomaType, 'id'> | null, o2: Pick<IDiplomaType, 'id'> | null): boolean {
    return o1 && o2 ? this.getDiplomaTypeIdentifier(o1) === this.getDiplomaTypeIdentifier(o2) : o1 === o2;
  }

  addDiplomaTypeToCollectionIfMissing<Type extends Pick<IDiplomaType, 'id'>>(
    diplomaTypeCollection: Type[],
    ...diplomaTypesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const diplomaTypes: Type[] = diplomaTypesToCheck.filter(isPresent);
    if (diplomaTypes.length > 0) {
      const diplomaTypeCollectionIdentifiers = diplomaTypeCollection.map(
        diplomaTypeItem => this.getDiplomaTypeIdentifier(diplomaTypeItem)!,
      );
      const diplomaTypesToAdd = diplomaTypes.filter(diplomaTypeItem => {
        const diplomaTypeIdentifier = this.getDiplomaTypeIdentifier(diplomaTypeItem);
        if (diplomaTypeCollectionIdentifiers.includes(diplomaTypeIdentifier)) {
          return false;
        }
        diplomaTypeCollectionIdentifiers.push(diplomaTypeIdentifier);
        return true;
      });
      return [...diplomaTypesToAdd, ...diplomaTypeCollection];
    }
    return diplomaTypeCollection;
  }
}
