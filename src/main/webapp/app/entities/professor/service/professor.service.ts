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
import { IProfessor, NewProfessor } from '../professor.model';

export type PartialUpdateProfessor = Partial<IProfessor> & Pick<IProfessor, 'id'>;

type RestOf<T extends IProfessor | NewProfessor> = Omit<T, 'birthdate'> & {
  birthdate?: string | null;
};

export type RestProfessor = RestOf<IProfessor>;

export type NewRestProfessor = RestOf<NewProfessor>;

export type PartialUpdateRestProfessor = RestOf<PartialUpdateProfessor>;

export type EntityResponseType = HttpResponse<IProfessor>;
export type EntityArrayResponseType = HttpResponse<IProfessor[]>;

@Injectable({ providedIn: 'root' })
export class ProfessorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/professors');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/professors/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(professor: NewProfessor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(professor);
    return this.http
      .post<RestProfessor>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(professor: IProfessor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(professor);
    return this.http
      .put<RestProfessor>(`${this.resourceUrl}/${this.getProfessorIdentifier(professor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(professor: PartialUpdateProfessor): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(professor);
    return this.http
      .patch<RestProfessor>(`${this.resourceUrl}/${this.getProfessorIdentifier(professor)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestProfessor>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestProfessor[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestProfessor[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<IProfessor[]>()], asapScheduler)),
    );
  }

  getProfessorIdentifier(professor: Pick<IProfessor, 'id'>): number {
    return professor.id;
  }

  compareProfessor(o1: Pick<IProfessor, 'id'> | null, o2: Pick<IProfessor, 'id'> | null): boolean {
    return o1 && o2 ? this.getProfessorIdentifier(o1) === this.getProfessorIdentifier(o2) : o1 === o2;
  }

  addProfessorToCollectionIfMissing<Type extends Pick<IProfessor, 'id'>>(
    professorCollection: Type[],
    ...professorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const professors: Type[] = professorsToCheck.filter(isPresent);
    if (professors.length > 0) {
      const professorCollectionIdentifiers = professorCollection.map(professorItem => this.getProfessorIdentifier(professorItem)!);
      const professorsToAdd = professors.filter(professorItem => {
        const professorIdentifier = this.getProfessorIdentifier(professorItem);
        if (professorCollectionIdentifiers.includes(professorIdentifier)) {
          return false;
        }
        professorCollectionIdentifiers.push(professorIdentifier);
        return true;
      });
      return [...professorsToAdd, ...professorCollection];
    }
    return professorCollection;
  }

  protected convertDateFromClient<T extends IProfessor | NewProfessor | PartialUpdateProfessor>(professor: T): RestOf<T> {
    return {
      ...professor,
      birthdate: professor.birthdate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restProfessor: RestProfessor): IProfessor {
    return {
      ...restProfessor,
      birthdate: restProfessor.birthdate ? dayjs(restProfessor.birthdate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestProfessor>): HttpResponse<IProfessor> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestProfessor[]>): HttpResponse<IProfessor[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
