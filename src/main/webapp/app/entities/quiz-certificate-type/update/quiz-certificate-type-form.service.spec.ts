import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../quiz-certificate-type.test-samples';

import { QuizCertificateTypeFormService } from './quiz-certificate-type-form.service';

describe('QuizCertificateType Form Service', () => {
  let service: QuizCertificateTypeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuizCertificateTypeFormService);
  });

  describe('Service methods', () => {
    describe('createQuizCertificateTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createQuizCertificateTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
          }),
        );
      });

      it('passing IQuizCertificateType should create a new form with FormGroup', () => {
        const formGroup = service.createQuizCertificateTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
          }),
        );
      });
    });

    describe('getQuizCertificateType', () => {
      it('should return NewQuizCertificateType for default QuizCertificateType initial value', () => {
        const formGroup = service.createQuizCertificateTypeFormGroup(sampleWithNewData);

        const quizCertificateType = service.getQuizCertificateType(formGroup) as any;

        expect(quizCertificateType).toMatchObject(sampleWithNewData);
      });

      it('should return NewQuizCertificateType for empty QuizCertificateType initial value', () => {
        const formGroup = service.createQuizCertificateTypeFormGroup();

        const quizCertificateType = service.getQuizCertificateType(formGroup) as any;

        expect(quizCertificateType).toMatchObject({});
      });

      it('should return IQuizCertificateType', () => {
        const formGroup = service.createQuizCertificateTypeFormGroup(sampleWithRequiredData);

        const quizCertificateType = service.getQuizCertificateType(formGroup) as any;

        expect(quizCertificateType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IQuizCertificateType should not enable id FormControl', () => {
        const formGroup = service.createQuizCertificateTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewQuizCertificateType should disable id FormControl', () => {
        const formGroup = service.createQuizCertificateTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
