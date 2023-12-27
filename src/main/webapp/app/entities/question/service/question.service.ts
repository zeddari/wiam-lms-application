import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IQuestion, NewQuestion } from '../question.model';

export type PartialUpdateQuestion = Partial<IQuestion> & Pick<IQuestion, 'id'>;

export type EntityResponseType = HttpResponse<IQuestion>;
export type EntityArrayResponseType = HttpResponse<IQuestion[]>;

@Injectable({ providedIn: 'root' })
export class QuestionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/questions');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/questions/_search');

  constructor(
    protected http: HttpClient,
    protected applicationConfigService: ApplicationConfigService,
  ) {}

  create(question: NewQuestion): Observable<EntityResponseType> {
    return this.http.post<IQuestion>(this.resourceUrl, question, { observe: 'response' });
  }

  update(question: IQuestion): Observable<EntityResponseType> {
    return this.http.put<IQuestion>(`${this.resourceUrl}/${this.getQuestionIdentifier(question)}`, question, { observe: 'response' });
  }

  partialUpdate(question: PartialUpdateQuestion): Observable<EntityResponseType> {
    return this.http.patch<IQuestion>(`${this.resourceUrl}/${this.getQuestionIdentifier(question)}`, question, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IQuestion>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IQuestion[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IQuestion[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IQuestion[]>()], asapScheduler)));
  }

  getQuestionIdentifier(question: Pick<IQuestion, 'id'>): number {
    return question.id;
  }

  compareQuestion(o1: Pick<IQuestion, 'id'> | null, o2: Pick<IQuestion, 'id'> | null): boolean {
    return o1 && o2 ? this.getQuestionIdentifier(o1) === this.getQuestionIdentifier(o2) : o1 === o2;
  }

  addQuestionToCollectionIfMissing<Type extends Pick<IQuestion, 'id'>>(
    questionCollection: Type[],
    ...questionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const questions: Type[] = questionsToCheck.filter(isPresent);
    if (questions.length > 0) {
      const questionCollectionIdentifiers = questionCollection.map(questionItem => this.getQuestionIdentifier(questionItem)!);
      const questionsToAdd = questions.filter(questionItem => {
        const questionIdentifier = this.getQuestionIdentifier(questionItem);
        if (questionCollectionIdentifiers.includes(questionIdentifier)) {
          return false;
        }
        questionCollectionIdentifiers.push(questionIdentifier);
        return true;
      });
      return [...questionsToAdd, ...questionCollection];
    }
    return questionCollection;
  }
}
