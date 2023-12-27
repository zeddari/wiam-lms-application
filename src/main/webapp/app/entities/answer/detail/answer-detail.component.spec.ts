import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AnswerDetailComponent } from './answer-detail.component';

describe('Answer Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnswerDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: AnswerDetailComponent,
              resolve: { answer: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(AnswerDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load answer on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', AnswerDetailComponent);

      // THEN
      expect(instance.answer).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
