import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionLinkDetailComponent } from './session-link-detail.component';

describe('SessionLink Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionLinkDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SessionLinkDetailComponent,
              resolve: { sessionLink: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SessionLinkDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load sessionLink on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SessionLinkDetailComponent);

      // THEN
      expect(instance.sessionLink).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
