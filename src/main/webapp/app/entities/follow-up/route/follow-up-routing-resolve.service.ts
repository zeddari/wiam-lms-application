import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFollowUp } from '../follow-up.model';
import { FollowUpService } from '../service/follow-up.service';

export const followUpResolve = (route: ActivatedRouteSnapshot): Observable<null | IFollowUp> => {
  const id = route.params['id'];
  if (id) {
    return inject(FollowUpService)
      .find(id)
      .pipe(
        mergeMap((followUp: HttpResponse<IFollowUp>) => {
          if (followUp.body) {
            return of(followUp.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default followUpResolve;
