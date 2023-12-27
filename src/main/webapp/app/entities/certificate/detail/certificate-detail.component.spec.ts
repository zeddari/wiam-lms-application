import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CertificateDetailComponent } from './certificate-detail.component';

describe('Certificate Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CertificateDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CertificateDetailComponent,
              resolve: { certificate: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CertificateDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load certificate on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CertificateDetailComponent);

      // THEN
      expect(instance.certificate).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
