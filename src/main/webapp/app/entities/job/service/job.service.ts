import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IJob, NewJob } from '../job.model';

export type PartialUpdateJob = Partial<IJob> & Pick<IJob, 'id'>;

type RestOf<T extends IJob | NewJob> = Omit<T, 'creationDate'> & {
  creationDate?: string | null;
};

export type RestJob = RestOf<IJob>;

export type NewRestJob = RestOf<NewJob>;

export type PartialUpdateRestJob = RestOf<PartialUpdateJob>;

export type EntityResponseType = HttpResponse<IJob>;
export type EntityArrayResponseType = HttpResponse<IJob[]>;

@Injectable({ providedIn: 'root' })
export class JobService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/jobs');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/jobs/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(job: NewJob): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(job);
    return this.http.post<RestJob>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(job: IJob): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(job);
    return this.http
      .put<RestJob>(`${this.resourceUrl}/${this.getJobIdentifier(job)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(job: PartialUpdateJob): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(job);
    return this.http
      .patch<RestJob>(`${this.resourceUrl}/${this.getJobIdentifier(job)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestJob>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestJob[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestJob[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([new HttpResponse<IJob[]>()], asapScheduler)),
    );
  }

  getJobIdentifier(job: Pick<IJob, 'id'>): number {
    return job.id;
  }

  compareJob(o1: Pick<IJob, 'id'> | null, o2: Pick<IJob, 'id'> | null): boolean {
    return o1 && o2 ? this.getJobIdentifier(o1) === this.getJobIdentifier(o2) : o1 === o2;
  }

  addJobToCollectionIfMissing<Type extends Pick<IJob, 'id'>>(jobCollection: Type[], ...jobsToCheck: (Type | null | undefined)[]): Type[] {
    const jobs: Type[] = jobsToCheck.filter(isPresent);
    if (jobs.length > 0) {
      const jobCollectionIdentifiers = jobCollection.map(jobItem => this.getJobIdentifier(jobItem)!);
      const jobsToAdd = jobs.filter(jobItem => {
        const jobIdentifier = this.getJobIdentifier(jobItem);
        if (jobCollectionIdentifiers.includes(jobIdentifier)) {
          return false;
        }
        jobCollectionIdentifiers.push(jobIdentifier);
        return true;
      });
      return [...jobsToAdd, ...jobCollection];
    }
    return jobCollection;
  }

  protected convertDateFromClient<T extends IJob | NewJob | PartialUpdateJob>(job: T): RestOf<T> {
    return {
      ...job,
      creationDate: job.creationDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restJob: RestJob): IJob {
    return {
      ...restJob,
      creationDate: restJob.creationDate ? dayjs(restJob.creationDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestJob>): HttpResponse<IJob> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestJob[]>): HttpResponse<IJob[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
