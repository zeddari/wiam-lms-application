import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../answer.test-samples';

import { AnswerFormService } from './answer-form.service';

describe('Answer Form Service', () => {
  let service: AnswerFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AnswerFormService);
  });

  describe('Service methods', () => {
    describe('createAnswerFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAnswerFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            a1v: expect.any(Object),
            a2v: expect.any(Object),
            a3v: expect.any(Object),
            a4v: expect.any(Object),
            result: expect.any(Object),
            question: expect.any(Object),
            student: expect.any(Object),
          }),
        );
      });

      it('passing IAnswer should create a new form with FormGroup', () => {
        const formGroup = service.createAnswerFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            a1v: expect.any(Object),
            a2v: expect.any(Object),
            a3v: expect.any(Object),
            a4v: expect.any(Object),
            result: expect.any(Object),
            question: expect.any(Object),
            student: expect.any(Object),
          }),
        );
      });
    });

    describe('getAnswer', () => {
      it('should return NewAnswer for default Answer initial value', () => {
        const formGroup = service.createAnswerFormGroup(sampleWithNewData);

        const answer = service.getAnswer(formGroup) as any;

        expect(answer).toMatchObject(sampleWithNewData);
      });

      it('should return NewAnswer for empty Answer initial value', () => {
        const formGroup = service.createAnswerFormGroup();

        const answer = service.getAnswer(formGroup) as any;

        expect(answer).toMatchObject({});
      });

      it('should return IAnswer', () => {
        const formGroup = service.createAnswerFormGroup(sampleWithRequiredData);

        const answer = service.getAnswer(formGroup) as any;

        expect(answer).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAnswer should not enable id FormControl', () => {
        const formGroup = service.createAnswerFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAnswer should disable id FormControl', () => {
        const formGroup = service.createAnswerFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
