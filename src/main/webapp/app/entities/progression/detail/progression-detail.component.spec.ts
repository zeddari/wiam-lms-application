import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProgressionDetailComponent } from './progression-detail.component';

describe('Progression Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgressionDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ProgressionDetailComponent,
              resolve: { progression: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ProgressionDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load progression on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ProgressionDetailComponent);

      // THEN
      expect(instance.progression).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
