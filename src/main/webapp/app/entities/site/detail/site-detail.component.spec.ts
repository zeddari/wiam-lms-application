import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SiteDetailComponent } from './site-detail.component';

describe('Site Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SiteDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SiteDetailComponent,
              resolve: { site: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SiteDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load site on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SiteDetailComponent);

      // THEN
      expect(instance.site).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
