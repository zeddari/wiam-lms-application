import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DepartementDetailComponent } from './departement-detail.component';

describe('Departement Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepartementDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: DepartementDetailComponent,
              resolve: { departement: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DepartementDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load departement on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DepartementDetailComponent);

      // THEN
      expect(instance.departement).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
