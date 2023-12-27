import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IQuizCertificate } from '../quiz-certificate.model';
import { QuizCertificateService } from '../service/quiz-certificate.service';

export const quizCertificateResolve = (route: ActivatedRouteSnapshot): Observable<null | IQuizCertificate> => {
  const id = route.params['id'];
  if (id) {
    return inject(QuizCertificateService)
      .find(id)
      .pipe(
        mergeMap((quizCertificate: HttpResponse<IQuizCertificate>) => {
          if (quizCertificate.body) {
            return of(quizCertificate.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default quizCertificateResolve;
