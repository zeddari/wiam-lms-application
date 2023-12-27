import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISite } from '../site.model';
import { SiteService } from '../service/site.service';

export const siteResolve = (route: ActivatedRouteSnapshot): Observable<null | ISite> => {
  const id = route.params['id'];
  if (id) {
    return inject(SiteService)
      .find(id)
      .pipe(
        mergeMap((site: HttpResponse<ISite>) => {
          if (site.body) {
            return of(site.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default siteResolve;
