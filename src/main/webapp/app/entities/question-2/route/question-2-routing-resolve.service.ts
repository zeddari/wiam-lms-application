import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IQuestion2 } from '../question-2.model';
import { Question2Service } from '../service/question-2.service';

export const question2Resolve = (route: ActivatedRouteSnapshot): Observable<null | IQuestion2> => {
  const id = route.params['id'];
  if (id) {
    return inject(Question2Service)
      .find(id)
      .pipe(
        mergeMap((question2: HttpResponse<IQuestion2>) => {
          if (question2.body) {
            return of(question2.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default question2Resolve;
