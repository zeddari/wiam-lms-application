import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SponsoringDetailComponent } from './sponsoring-detail.component';

describe('Sponsoring Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SponsoringDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SponsoringDetailComponent,
              resolve: { sponsoring: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SponsoringDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load sponsoring on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SponsoringDetailComponent);

      // THEN
      expect(instance.sponsoring).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
