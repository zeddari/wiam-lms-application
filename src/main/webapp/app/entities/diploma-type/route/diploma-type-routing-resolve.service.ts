import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDiplomaType } from '../diploma-type.model';
import { DiplomaTypeService } from '../service/diploma-type.service';

export const diplomaTypeResolve = (route: ActivatedRouteSnapshot): Observable<null | IDiplomaType> => {
  const id = route.params['id'];
  if (id) {
    return inject(DiplomaTypeService)
      .find(id)
      .pipe(
        mergeMap((diplomaType: HttpResponse<IDiplomaType>) => {
          if (diplomaType.body) {
            return of(diplomaType.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default diplomaTypeResolve;
