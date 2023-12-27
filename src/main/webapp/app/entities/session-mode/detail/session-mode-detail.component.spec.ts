import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionModeDetailComponent } from './session-mode-detail.component';

describe('SessionMode Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionModeDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SessionModeDetailComponent,
              resolve: { sessionMode: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SessionModeDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load sessionMode on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SessionModeDetailComponent);

      // THEN
      expect(instance.sessionMode).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
