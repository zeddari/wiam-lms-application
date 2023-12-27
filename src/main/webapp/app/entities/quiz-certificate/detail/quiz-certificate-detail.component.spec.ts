import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { QuizCertificateDetailComponent } from './quiz-certificate-detail.component';

describe('QuizCertificate Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuizCertificateDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: QuizCertificateDetailComponent,
              resolve: { quizCertificate: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(QuizCertificateDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load quizCertificate on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', QuizCertificateDetailComponent);

      // THEN
      expect(instance.quizCertificate).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
