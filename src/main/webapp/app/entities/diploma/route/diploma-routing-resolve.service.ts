import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDiploma } from '../diploma.model';
import { DiplomaService } from '../service/diploma.service';

export const diplomaResolve = (route: ActivatedRouteSnapshot): Observable<null | IDiploma> => {
  const id = route.params['id'];
  if (id) {
    return inject(DiplomaService)
      .find(id)
      .pipe(
        mergeMap((diploma: HttpResponse<IDiploma>) => {
          if (diploma.body) {
            return of(diploma.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default diplomaResolve;
