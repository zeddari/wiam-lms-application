import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISessionLink } from '../session-link.model';
import { SessionLinkService } from '../service/session-link.service';

export const sessionLinkResolve = (route: ActivatedRouteSnapshot): Observable<null | ISessionLink> => {
  const id = route.params['id'];
  if (id) {
    return inject(SessionLinkService)
      .find(id)
      .pipe(
        mergeMap((sessionLink: HttpResponse<ISessionLink>) => {
          if (sessionLink.body) {
            return of(sessionLink.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default sessionLinkResolve;
