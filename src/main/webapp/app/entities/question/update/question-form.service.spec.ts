import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../question.test-samples';

import { QuestionFormService } from './question-form.service';

describe('Question Form Service', () => {
  let service: QuestionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuestionFormService);
  });

  describe('Service methods', () => {
    describe('createQuestionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createQuestionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            question: expect.any(Object),
            note: expect.any(Object),
            a1: expect.any(Object),
            a1v: expect.any(Object),
            a2: expect.any(Object),
            a2v: expect.any(Object),
            a3: expect.any(Object),
            a3v: expect.any(Object),
            a4: expect.any(Object),
            a4v: expect.any(Object),
            isactive: expect.any(Object),
            questionTitle: expect.any(Object),
            questionType: expect.any(Object),
            questionDescription: expect.any(Object),
            questionPoint: expect.any(Object),
            questionSubject: expect.any(Object),
            questionStatus: expect.any(Object),
            course: expect.any(Object),
          }),
        );
      });

      it('passing IQuestion should create a new form with FormGroup', () => {
        const formGroup = service.createQuestionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            question: expect.any(Object),
            note: expect.any(Object),
            a1: expect.any(Object),
            a1v: expect.any(Object),
            a2: expect.any(Object),
            a2v: expect.any(Object),
            a3: expect.any(Object),
            a3v: expect.any(Object),
            a4: expect.any(Object),
            a4v: expect.any(Object),
            isactive: expect.any(Object),
            questionTitle: expect.any(Object),
            questionType: expect.any(Object),
            questionDescription: expect.any(Object),
            questionPoint: expect.any(Object),
            questionSubject: expect.any(Object),
            questionStatus: expect.any(Object),
            course: expect.any(Object),
          }),
        );
      });
    });

    describe('getQuestion', () => {
      it('should return NewQuestion for default Question initial value', () => {
        const formGroup = service.createQuestionFormGroup(sampleWithNewData);

        const question = service.getQuestion(formGroup) as any;

        expect(question).toMatchObject(sampleWithNewData);
      });

      it('should return NewQuestion for empty Question initial value', () => {
        const formGroup = service.createQuestionFormGroup();

        const question = service.getQuestion(formGroup) as any;

        expect(question).toMatchObject({});
      });

      it('should return IQuestion', () => {
        const formGroup = service.createQuestionFormGroup(sampleWithRequiredData);

        const question = service.getQuestion(formGroup) as any;

        expect(question).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IQuestion should not enable id FormControl', () => {
        const formGroup = service.createQuestionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewQuestion should disable id FormControl', () => {
        const formGroup = service.createQuestionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
