import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../diploma.test-samples';

import { DiplomaFormService } from './diploma-form.service';

describe('Diploma Form Service', () => {
  let service: DiplomaFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DiplomaFormService);
  });

  describe('Service methods', () => {
    describe('createDiplomaFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDiplomaFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            subject: expect.any(Object),
            detail: expect.any(Object),
            supervisor: expect.any(Object),
            grade: expect.any(Object),
            graduationDate: expect.any(Object),
            school: expect.any(Object),
            userCustom: expect.any(Object),
            diplomaType: expect.any(Object),
          }),
        );
      });

      it('passing IDiploma should create a new form with FormGroup', () => {
        const formGroup = service.createDiplomaFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            subject: expect.any(Object),
            detail: expect.any(Object),
            supervisor: expect.any(Object),
            grade: expect.any(Object),
            graduationDate: expect.any(Object),
            school: expect.any(Object),
            userCustom: expect.any(Object),
            diplomaType: expect.any(Object),
          }),
        );
      });
    });

    describe('getDiploma', () => {
      it('should return NewDiploma for default Diploma initial value', () => {
        const formGroup = service.createDiplomaFormGroup(sampleWithNewData);

        const diploma = service.getDiploma(formGroup) as any;

        expect(diploma).toMatchObject(sampleWithNewData);
      });

      it('should return NewDiploma for empty Diploma initial value', () => {
        const formGroup = service.createDiplomaFormGroup();

        const diploma = service.getDiploma(formGroup) as any;

        expect(diploma).toMatchObject({});
      });

      it('should return IDiploma', () => {
        const formGroup = service.createDiplomaFormGroup(sampleWithRequiredData);

        const diploma = service.getDiploma(formGroup) as any;

        expect(diploma).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDiploma should not enable id FormControl', () => {
        const formGroup = service.createDiplomaFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDiploma should disable id FormControl', () => {
        const formGroup = service.createDiplomaFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
