import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISessionProvider } from '../session-provider.model';
import { SessionProviderService } from '../service/session-provider.service';

export const sessionProviderResolve = (route: ActivatedRouteSnapshot): Observable<null | ISessionProvider> => {
  const id = route.params['id'];
  if (id) {
    return inject(SessionProviderService)
      .find(id)
      .pipe(
        mergeMap((sessionProvider: HttpResponse<ISessionProvider>) => {
          if (sessionProvider.body) {
            return of(sessionProvider.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default sessionProviderResolve;
