import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserCustom } from '../user-custom.model';
import { UserCustomService } from '../service/user-custom.service';

export const userCustomResolve = (route: ActivatedRouteSnapshot): Observable<null | IUserCustom> => {
  const id = route.params['id'];
  if (id) {
    return inject(UserCustomService)
      .find(id)
      .pipe(
        mergeMap((userCustom: HttpResponse<IUserCustom>) => {
          if (userCustom.body) {
            return of(userCustom.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default userCustomResolve;
