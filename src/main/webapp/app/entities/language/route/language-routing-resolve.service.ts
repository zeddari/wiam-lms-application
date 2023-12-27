import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILanguage } from '../language.model';
import { LanguageService } from '../service/language.service';

export const languageResolve = (route: ActivatedRouteSnapshot): Observable<null | ILanguage> => {
  const id = route.params['id'];
  if (id) {
    return inject(LanguageService)
      .find(id)
      .pipe(
        mergeMap((language: HttpResponse<ILanguage>) => {
          if (language.body) {
            return of(language.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default languageResolve;
