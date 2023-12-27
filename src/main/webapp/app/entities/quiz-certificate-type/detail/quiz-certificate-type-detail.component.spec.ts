import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { QuizCertificateTypeDetailComponent } from './quiz-certificate-type-detail.component';

describe('QuizCertificateType Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuizCertificateTypeDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: QuizCertificateTypeDetailComponent,
              resolve: { quizCertificateType: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(QuizCertificateTypeDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load quizCertificateType on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', QuizCertificateTypeDetailComponent);

      // THEN
      expect(instance.quizCertificateType).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
