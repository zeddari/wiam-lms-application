import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../enrolement.test-samples';

import { EnrolementFormService } from './enrolement-form.service';

describe('Enrolement Form Service', () => {
  let service: EnrolementFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnrolementFormService);
  });

  describe('Service methods', () => {
    describe('createEnrolementFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createEnrolementFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            isActive: expect.any(Object),
            activatedAt: expect.any(Object),
            activatedBy: expect.any(Object),
            enrolmentStartTime: expect.any(Object),
            enrolemntEndTime: expect.any(Object),
            student: expect.any(Object),
            course: expect.any(Object),
          }),
        );
      });

      it('passing IEnrolement should create a new form with FormGroup', () => {
        const formGroup = service.createEnrolementFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            isActive: expect.any(Object),
            activatedAt: expect.any(Object),
            activatedBy: expect.any(Object),
            enrolmentStartTime: expect.any(Object),
            enrolemntEndTime: expect.any(Object),
            student: expect.any(Object),
            course: expect.any(Object),
          }),
        );
      });
    });

    describe('getEnrolement', () => {
      it('should return NewEnrolement for default Enrolement initial value', () => {
        const formGroup = service.createEnrolementFormGroup(sampleWithNewData);

        const enrolement = service.getEnrolement(formGroup) as any;

        expect(enrolement).toMatchObject(sampleWithNewData);
      });

      it('should return NewEnrolement for empty Enrolement initial value', () => {
        const formGroup = service.createEnrolementFormGroup();

        const enrolement = service.getEnrolement(formGroup) as any;

        expect(enrolement).toMatchObject({});
      });

      it('should return IEnrolement', () => {
        const formGroup = service.createEnrolementFormGroup(sampleWithRequiredData);

        const enrolement = service.getEnrolement(formGroup) as any;

        expect(enrolement).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IEnrolement should not enable id FormControl', () => {
        const formGroup = service.createEnrolementFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewEnrolement should disable id FormControl', () => {
        const formGroup = service.createEnrolementFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
