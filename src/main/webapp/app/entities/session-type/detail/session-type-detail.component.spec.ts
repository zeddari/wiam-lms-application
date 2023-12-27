import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionTypeDetailComponent } from './session-type-detail.component';

describe('SessionType Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionTypeDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SessionTypeDetailComponent,
              resolve: { sessionType: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SessionTypeDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load sessionType on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SessionTypeDetailComponent);

      // THEN
      expect(instance.sessionType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
