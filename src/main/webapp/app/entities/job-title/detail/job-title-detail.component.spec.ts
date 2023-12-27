import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { JobTitleDetailComponent } from './job-title-detail.component';

describe('JobTitle Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobTitleDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: JobTitleDetailComponent,
              resolve: { jobTitle: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(JobTitleDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load jobTitle on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', JobTitleDetailComponent);

      // THEN
      expect(instance.jobTitle).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
