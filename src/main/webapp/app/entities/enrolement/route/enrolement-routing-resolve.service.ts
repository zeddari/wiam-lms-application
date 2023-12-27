import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEnrolement } from '../enrolement.model';
import { EnrolementService } from '../service/enrolement.service';

export const enrolementResolve = (route: ActivatedRouteSnapshot): Observable<null | IEnrolement> => {
  const id = route.params['id'];
  if (id) {
    return inject(EnrolementService)
      .find(id)
      .pipe(
        mergeMap((enrolement: HttpResponse<IEnrolement>) => {
          if (enrolement.body) {
            return of(enrolement.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default enrolementResolve;
