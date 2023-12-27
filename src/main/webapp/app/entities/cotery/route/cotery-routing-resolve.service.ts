import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICotery } from '../cotery.model';
import { CoteryService } from '../service/cotery.service';

export const coteryResolve = (route: ActivatedRouteSnapshot): Observable<null | ICotery> => {
  const id = route.params['id'];
  if (id) {
    return inject(CoteryService)
      .find(id)
      .pipe(
        mergeMap((cotery: HttpResponse<ICotery>) => {
          if (cotery.body) {
            return of(cotery.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default coteryResolve;
