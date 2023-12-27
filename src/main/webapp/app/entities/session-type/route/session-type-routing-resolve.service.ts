import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISessionType } from '../session-type.model';
import { SessionTypeService } from '../service/session-type.service';

export const sessionTypeResolve = (route: ActivatedRouteSnapshot): Observable<null | ISessionType> => {
  const id = route.params['id'];
  if (id) {
    return inject(SessionTypeService)
      .find(id)
      .pipe(
        mergeMap((sessionType: HttpResponse<ISessionType>) => {
          if (sessionType.body) {
            return of(sessionType.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default sessionTypeResolve;
