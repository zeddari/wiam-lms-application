import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IQuiz } from '../quiz.model';
import { QuizService } from '../service/quiz.service';

export const quizResolve = (route: ActivatedRouteSnapshot): Observable<null | IQuiz> => {
  const id = route.params['id'];
  if (id) {
    return inject(QuizService)
      .find(id)
      .pipe(
        mergeMap((quiz: HttpResponse<IQuiz>) => {
          if (quiz.body) {
            return of(quiz.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default quizResolve;
