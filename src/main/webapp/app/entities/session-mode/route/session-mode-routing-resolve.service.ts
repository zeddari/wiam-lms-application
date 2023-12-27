import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISessionMode } from '../session-mode.model';
import { SessionModeService } from '../service/session-mode.service';

export const sessionModeResolve = (route: ActivatedRouteSnapshot): Observable<null | ISessionMode> => {
  const id = route.params['id'];
  if (id) {
    return inject(SessionModeService)
      .find(id)
      .pipe(
        mergeMap((sessionMode: HttpResponse<ISessionMode>) => {
          if (sessionMode.body) {
            return of(sessionMode.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default sessionModeResolve;
