import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISessionJoinMode } from '../session-join-mode.model';
import { SessionJoinModeService } from '../service/session-join-mode.service';

export const sessionJoinModeResolve = (route: ActivatedRouteSnapshot): Observable<null | ISessionJoinMode> => {
  const id = route.params['id'];
  if (id) {
    return inject(SessionJoinModeService)
      .find(id)
      .pipe(
        mergeMap((sessionJoinMode: HttpResponse<ISessionJoinMode>) => {
          if (sessionJoinMode.body) {
            return of(sessionJoinMode.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default sessionJoinModeResolve;
