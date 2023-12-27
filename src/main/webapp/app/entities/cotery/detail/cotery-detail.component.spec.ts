import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CoteryDetailComponent } from './cotery-detail.component';

describe('Cotery Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CoteryDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CoteryDetailComponent,
              resolve: { cotery: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CoteryDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load cotery on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CoteryDetailComponent);

      // THEN
      expect(instance.cotery).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
