import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { Country2DetailComponent } from './country-2-detail.component';

describe('Country2 Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Country2DetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: Country2DetailComponent,
              resolve: { country2: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(Country2DetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load country2 on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', Country2DetailComponent);

      // THEN
      expect(instance.country2).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
