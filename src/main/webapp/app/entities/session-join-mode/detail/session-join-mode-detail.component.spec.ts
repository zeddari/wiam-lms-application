import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionJoinModeDetailComponent } from './session-join-mode-detail.component';

describe('SessionJoinMode Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionJoinModeDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SessionJoinModeDetailComponent,
              resolve: { sessionJoinMode: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SessionJoinModeDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load sessionJoinMode on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SessionJoinModeDetailComponent);

      // THEN
      expect(instance.sessionJoinMode).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
