import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IQuizCertificateType } from '../quiz-certificate-type.model';
import { QuizCertificateTypeService } from '../service/quiz-certificate-type.service';

export const quizCertificateTypeResolve = (route: ActivatedRouteSnapshot): Observable<null | IQuizCertificateType> => {
  const id = route.params['id'];
  if (id) {
    return inject(QuizCertificateTypeService)
      .find(id)
      .pipe(
        mergeMap((quizCertificateType: HttpResponse<IQuizCertificateType>) => {
          if (quizCertificateType.body) {
            return of(quizCertificateType.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default quizCertificateTypeResolve;
