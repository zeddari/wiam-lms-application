import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGroup } from '../group.model';
import { GroupService } from '../service/group.service';

export const groupResolve = (route: ActivatedRouteSnapshot): Observable<null | IGroup> => {
  const id = route.params['id'];
  if (id) {
    return inject(GroupService)
      .find(id)
      .pipe(
        mergeMap((group: HttpResponse<IGroup>) => {
          if (group.body) {
            return of(group.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default groupResolve;
