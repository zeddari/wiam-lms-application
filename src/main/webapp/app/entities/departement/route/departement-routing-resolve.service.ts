import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDepartement } from '../departement.model';
import { DepartementService } from '../service/departement.service';

export const departementResolve = (route: ActivatedRouteSnapshot): Observable<null | IDepartement> => {
  const id = route.params['id'];
  if (id) {
    return inject(DepartementService)
      .find(id)
      .pipe(
        mergeMap((departement: HttpResponse<IDepartement>) => {
          if (departement.body) {
            return of(departement.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default departementResolve;
