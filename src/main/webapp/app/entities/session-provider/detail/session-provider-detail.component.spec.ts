import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionProviderDetailComponent } from './session-provider-detail.component';

describe('SessionProvider Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionProviderDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SessionProviderDetailComponent,
              resolve: { sessionProvider: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SessionProviderDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load sessionProvider on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SessionProviderDetailComponent);

      // THEN
      expect(instance.sessionProvider).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
