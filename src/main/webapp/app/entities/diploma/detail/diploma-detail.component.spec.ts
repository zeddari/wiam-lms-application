import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DiplomaDetailComponent } from './diploma-detail.component';

describe('Diploma Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiplomaDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: DiplomaDetailComponent,
              resolve: { diploma: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DiplomaDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load diploma on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DiplomaDetailComponent);

      // THEN
      expect(instance.diploma).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
