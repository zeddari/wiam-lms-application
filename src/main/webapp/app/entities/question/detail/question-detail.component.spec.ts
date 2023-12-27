import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { QuestionDetailComponent } from './question-detail.component';

describe('Question Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuestionDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: QuestionDetailComponent,
              resolve: { question: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(QuestionDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load question on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', QuestionDetailComponent);

      // THEN
      expect(instance.question).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
