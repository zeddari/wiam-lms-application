import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EnrolementDetailComponent } from './enrolement-detail.component';

describe('Enrolement Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnrolementDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: EnrolementDetailComponent,
              resolve: { enrolement: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(EnrolementDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load enrolement on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', EnrolementDetailComponent);

      // THEN
      expect(instance.enrolement).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
