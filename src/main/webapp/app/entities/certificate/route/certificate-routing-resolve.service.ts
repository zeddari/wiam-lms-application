import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICertificate } from '../certificate.model';
import { CertificateService } from '../service/certificate.service';

export const certificateResolve = (route: ActivatedRouteSnapshot): Observable<null | ICertificate> => {
  const id = route.params['id'];
  if (id) {
    return inject(CertificateService)
      .find(id)
      .pipe(
        mergeMap((certificate: HttpResponse<ICertificate>) => {
          if (certificate.body) {
            return of(certificate.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default certificateResolve;
