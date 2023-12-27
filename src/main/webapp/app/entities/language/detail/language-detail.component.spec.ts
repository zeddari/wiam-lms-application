import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { LanguageDetailComponent } from './language-detail.component';

describe('Language Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LanguageDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: LanguageDetailComponent,
              resolve: { language: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(LanguageDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load language on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LanguageDetailComponent);

      // THEN
      expect(instance.language).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
