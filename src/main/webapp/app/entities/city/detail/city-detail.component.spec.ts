import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CityDetailComponent } from './city-detail.component';

describe('City Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CityDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CityDetailComponent,
              resolve: { city: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CityDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load city on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CityDetailComponent);

      // THEN
      expect(instance.city).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
