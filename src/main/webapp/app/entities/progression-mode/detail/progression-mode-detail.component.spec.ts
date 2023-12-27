import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProgressionModeDetailComponent } from './progression-mode-detail.component';

describe('ProgressionMode Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgressionModeDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ProgressionModeDetailComponent,
              resolve: { progressionMode: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ProgressionModeDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load progressionMode on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ProgressionModeDetailComponent);

      // THEN
      expect(instance.progressionMode).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
