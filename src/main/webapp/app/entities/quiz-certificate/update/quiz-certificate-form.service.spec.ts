import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../quiz-certificate.test-samples';

import { QuizCertificateFormService } from './quiz-certificate-form.service';

describe('QuizCertificate Form Service', () => {
  let service: QuizCertificateFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuizCertificateFormService);
  });

  describe('Service methods', () => {
    describe('createQuizCertificateFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createQuizCertificateFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            isActive: expect.any(Object),
            students: expect.any(Object),
            questions: expect.any(Object),
            part: expect.any(Object),
            session: expect.any(Object),
            type: expect.any(Object),
          }),
        );
      });

      it('passing IQuizCertificate should create a new form with FormGroup', () => {
        const formGroup = service.createQuizCertificateFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            isActive: expect.any(Object),
            students: expect.any(Object),
            questions: expect.any(Object),
            part: expect.any(Object),
            session: expect.any(Object),
            type: expect.any(Object),
          }),
        );
      });
    });

    describe('getQuizCertificate', () => {
      it('should return NewQuizCertificate for default QuizCertificate initial value', () => {
        const formGroup = service.createQuizCertificateFormGroup(sampleWithNewData);

        const quizCertificate = service.getQuizCertificate(formGroup) as any;

        expect(quizCertificate).toMatchObject(sampleWithNewData);
      });

      it('should return NewQuizCertificate for empty QuizCertificate initial value', () => {
        const formGroup = service.createQuizCertificateFormGroup();

        const quizCertificate = service.getQuizCertificate(formGroup) as any;

        expect(quizCertificate).toMatchObject({});
      });

      it('should return IQuizCertificate', () => {
        const formGroup = service.createQuizCertificateFormGroup(sampleWithRequiredData);

        const quizCertificate = service.getQuizCertificate(formGroup) as any;

        expect(quizCertificate).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IQuizCertificate should not enable id FormControl', () => {
        const formGroup = service.createQuizCertificateFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewQuizCertificate should disable id FormControl', () => {
        const formGroup = service.createQuizCertificateFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
