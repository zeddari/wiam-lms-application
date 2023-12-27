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
import { IStudent, NewStudent } from '../student.model';

export type PartialUpdateStudent = Partial<IStudent> & Pick<IStudent, 'id'>;

type RestOf<T extends IStudent | NewStudent> = Omit<T, 'birthdate'> & {
  birthdate?: string | null;
};

export type RestStudent = RestOf<IStudent>;

export type NewRestStudent = RestOf<NewStudent>;

export type PartialUpdateRestStudent = RestOf<PartialUpdateStudent>;

export type EntityResponseType = HttpResponse<IStudent>;
export type EntityArrayResponseType = HttpResponse<IStudent[]>;

@Injectable({ providedIn: 'root' })
export class StudentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/students');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/students/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(student: NewStudent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(student);
    return this.http
      .post<RestStudent>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(student: IStudent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(student);
    return this.http
      .put<RestStudent>(`${this.resourceUrl}/${this.getStudentIdentifier(student)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(student: PartialUpdateStudent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(student);
    return this.http
      .patch<RestStudent>(`${this.resourceUrl}/${this.getStudentIdentifier(student)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestStudent>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestStudent[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestStudent[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<IStudent[]>()], asapScheduler)),
    );
  }

  getStudentIdentifier(student: Pick<IStudent, 'id'>): number {
    return student.id;
  }

  compareStudent(o1: Pick<IStudent, 'id'> | null, o2: Pick<IStudent, 'id'> | null): boolean {
    return o1 && o2 ? this.getStudentIdentifier(o1) === this.getStudentIdentifier(o2) : o1 === o2;
  }

  addStudentToCollectionIfMissing<Type extends Pick<IStudent, 'id'>>(
    studentCollection: Type[],
    ...studentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const students: Type[] = studentsToCheck.filter(isPresent);
    if (students.length > 0) {
      const studentCollectionIdentifiers = studentCollection.map(studentItem => this.getStudentIdentifier(studentItem)!);
      const studentsToAdd = students.filter(studentItem => {
        const studentIdentifier = this.getStudentIdentifier(studentItem);
        if (studentCollectionIdentifiers.includes(studentIdentifier)) {
          return false;
        }
        studentCollectionIdentifiers.push(studentIdentifier);
        return true;
      });
      return [...studentsToAdd, ...studentCollection];
    }
    return studentCollection;
  }

  protected convertDateFromClient<T extends IStudent | NewStudent | PartialUpdateStudent>(student: T): RestOf<T> {
    return {
      ...student,
      birthdate: student.birthdate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertDateFromServer(restStudent: RestStudent): IStudent {
    return {
      ...restStudent,
      birthdate: restStudent.birthdate ? dayjs(restStudent.birthdate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestStudent>): HttpResponse<IStudent> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestStudent[]>): HttpResponse<IStudent[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
