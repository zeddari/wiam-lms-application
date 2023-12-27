import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../question-2.test-samples';

import { Question2FormService } from './question-2-form.service';

describe('Question2 Form Service', () => {
  let service: Question2FormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Question2FormService);
  });

  describe('Service methods', () => {
    describe('createQuestion2FormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createQuestion2FormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            questionTitle: expect.any(Object),
            questionType: expect.any(Object),
            questionDescription: expect.any(Object),
            questionPoint: expect.any(Object),
            questionSubject: expect.any(Object),
            questionStatus: expect.any(Object),
          }),
        );
      });

      it('passing IQuestion2 should create a new form with FormGroup', () => {
        const formGroup = service.createQuestion2FormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            questionTitle: expect.any(Object),
            questionType: expect.any(Object),
            questionDescription: expect.any(Object),
            questionPoint: expect.any(Object),
            questionSubject: expect.any(Object),
            questionStatus: expect.any(Object),
          }),
        );
      });
    });

    describe('getQuestion2', () => {
      it('should return NewQuestion2 for default Question2 initial value', () => {
        const formGroup = service.createQuestion2FormGroup(sampleWithNewData);

        const question2 = service.getQuestion2(formGroup) as any;

        expect(question2).toMatchObject(sampleWithNewData);
      });

      it('should return NewQuestion2 for empty Question2 initial value', () => {
        const formGroup = service.createQuestion2FormGroup();

        const question2 = service.getQuestion2(formGroup) as any;

        expect(question2).toMatchObject({});
      });

      it('should return IQuestion2', () => {
        const formGroup = service.createQuestion2FormGroup(sampleWithRequiredData);

        const question2 = service.getQuestion2(formGroup) as any;

        expect(question2).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IQuestion2 should not enable id FormControl', () => {
        const formGroup = service.createQuestion2FormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewQuestion2 should disable id FormControl', () => {
        const formGroup = service.createQuestion2FormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
