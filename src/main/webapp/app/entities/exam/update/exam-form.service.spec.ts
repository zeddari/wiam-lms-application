import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../exam.test-samples';

import { ExamFormService } from './exam-form.service';

describe('Exam Form Service', () => {
  let service: ExamFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExamFormService);
  });

  describe('Service methods', () => {
    describe('createExamFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createExamFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            comite: expect.any(Object),
            studentName: expect.any(Object),
            examName: expect.any(Object),
            examCategory: expect.any(Object),
            examType: expect.any(Object),
            tajweedScore: expect.any(Object),
            hifdScore: expect.any(Object),
            adaeScore: expect.any(Object),
            observation: expect.any(Object),
            decision: expect.any(Object),
          }),
        );
      });

      it('passing IExam should create a new form with FormGroup', () => {
        const formGroup = service.createExamFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            comite: expect.any(Object),
            studentName: expect.any(Object),
            examName: expect.any(Object),
            examCategory: expect.any(Object),
            examType: expect.any(Object),
            tajweedScore: expect.any(Object),
            hifdScore: expect.any(Object),
            adaeScore: expect.any(Object),
            observation: expect.any(Object),
            decision: expect.any(Object),
          }),
        );
      });
    });

    describe('getExam', () => {
      it('should return NewExam for default Exam initial value', () => {
        const formGroup = service.createExamFormGroup(sampleWithNewData);

        const exam = service.getExam(formGroup) as any;

        expect(exam).toMatchObject(sampleWithNewData);
      });

      it('should return NewExam for empty Exam initial value', () => {
        const formGroup = service.createExamFormGroup();

        const exam = service.getExam(formGroup) as any;

        expect(exam).toMatchObject({});
      });

      it('should return IExam', () => {
        const formGroup = service.createExamFormGroup(sampleWithRequiredData);

        const exam = service.getExam(formGroup) as any;

        expect(exam).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IExam should not enable id FormControl', () => {
        const formGroup = service.createExamFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewExam should disable id FormControl', () => {
        const formGroup = service.createExamFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
