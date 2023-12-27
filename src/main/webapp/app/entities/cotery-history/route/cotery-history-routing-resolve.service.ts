import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICoteryHistory } from '../cotery-history.model';
import { CoteryHistoryService } from '../service/cotery-history.service';

export const coteryHistoryResolve = (route: ActivatedRouteSnapshot): Observable<null | ICoteryHistory> => {
  const id = route.params['id'];
  if (id) {
    return inject(CoteryHistoryService)
      .find(id)
      .pipe(
        mergeMap((coteryHistory: HttpResponse<ICoteryHistory>) => {
          if (coteryHistory.body) {
            return of(coteryHistory.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default coteryHistoryResolve;
