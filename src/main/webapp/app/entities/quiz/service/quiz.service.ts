import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IQuiz, NewQuiz } from '../quiz.model';

export type PartialUpdateQuiz = Partial<IQuiz> & Pick<IQuiz, 'id'>;

export type EntityResponseType = HttpResponse<IQuiz>;
export type EntityArrayResponseType = HttpResponse<IQuiz[]>;

@Injectable({ providedIn: 'root' })
export class QuizService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/quizzes');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/quizzes/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(quiz: NewQuiz): Observable<EntityResponseType> {
    return this.http.post<IQuiz>(this.resourceUrl, quiz, { observe: 'response' });
  }

  update(quiz: IQuiz): Observable<EntityResponseType> {
    return this.http.put<IQuiz>(`${this.resourceUrl}/${this.getQuizIdentifier(quiz)}`, quiz, { observe: 'response' });
  }

  partialUpdate(quiz: PartialUpdateQuiz): Observable<EntityResponseType> {
    return this.http.patch<IQuiz>(`${this.resourceUrl}/${this.getQuizIdentifier(quiz)}`, quiz, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IQuiz>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IQuiz[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IQuiz[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IQuiz[]>()], asapScheduler)));
  }

  getQuizIdentifier(quiz: Pick<IQuiz, 'id'>): number {
    return quiz.id;
  }

  compareQuiz(o1: Pick<IQuiz, 'id'> | null, o2: Pick<IQuiz, 'id'> | null): boolean {
    return o1 && o2 ? this.getQuizIdentifier(o1) === this.getQuizIdentifier(o2) : o1 === o2;
  }

  addQuizToCollectionIfMissing<Type extends Pick<IQuiz, 'id'>>(
    quizCollection: Type[],
    ...quizzesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const quizzes: Type[] = quizzesToCheck.filter(isPresent);
    if (quizzes.length > 0) {
      const quizCollectionIdentifiers = quizCollection.map(quizItem => this.getQuizIdentifier(quizItem)!);
      const quizzesToAdd = quizzes.filter(quizItem => {
        const quizIdentifier = this.getQuizIdentifier(quizItem);
        if (quizCollectionIdentifiers.includes(quizIdentifier)) {
          return false;
        }
        quizCollectionIdentifiers.push(quizIdentifier);
        return true;
      });
      return [...quizzesToAdd, ...quizCollection];
    }
    return quizCollection;
  }
}
