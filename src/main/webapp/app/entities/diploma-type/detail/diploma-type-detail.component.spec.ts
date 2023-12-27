import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DiplomaTypeDetailComponent } from './diploma-type-detail.component';

describe('DiplomaType Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiplomaTypeDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: DiplomaTypeDetailComponent,
              resolve: { diplomaType: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DiplomaTypeDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load diplomaType on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DiplomaTypeDetailComponent);

      // THEN
      expect(instance.diplomaType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
