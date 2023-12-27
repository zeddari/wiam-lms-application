import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { QuizDetailComponent } from './quiz-detail.component';

describe('Quiz Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuizDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: QuizDetailComponent,
              resolve: { quiz: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(QuizDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load quiz on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', QuizDetailComponent);

      // THEN
      expect(instance.quiz).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
