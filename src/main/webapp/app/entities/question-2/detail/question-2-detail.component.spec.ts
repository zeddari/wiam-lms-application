import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { Question2DetailComponent } from './question-2-detail.component';

describe('Question2 Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Question2DetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: Question2DetailComponent,
              resolve: { question2: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(Question2DetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load question2 on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', Question2DetailComponent);

      // THEN
      expect(instance.question2).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
