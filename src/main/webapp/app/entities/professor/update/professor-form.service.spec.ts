import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../professor.test-samples';

import { ProfessorFormService } from './professor-form.service';

describe('Professor Form Service', () => {
  let service: ProfessorFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfessorFormService);
  });

  describe('Service methods', () => {
    describe('createProfessorFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProfessorFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            phoneNumber: expect.any(Object),
            mobileNumber: expect.any(Object),
            gender: expect.any(Object),
            about: expect.any(Object),
            imageLink: expect.any(Object),
            code: expect.any(Object),
            birthdate: expect.any(Object),
            lastDegree: expect.any(Object),
            userCustom: expect.any(Object),
          }),
        );
      });

      it('passing IProfessor should create a new form with FormGroup', () => {
        const formGroup = service.createProfessorFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            phoneNumber: expect.any(Object),
            mobileNumber: expect.any(Object),
            gender: expect.any(Object),
            about: expect.any(Object),
            imageLink: expect.any(Object),
            code: expect.any(Object),
            birthdate: expect.any(Object),
            lastDegree: expect.any(Object),
            userCustom: expect.any(Object),
          }),
        );
      });
    });

    describe('getProfessor', () => {
      it('should return NewProfessor for default Professor initial value', () => {
        const formGroup = service.createProfessorFormGroup(sampleWithNewData);

        const professor = service.getProfessor(formGroup) as any;

        expect(professor).toMatchObject(sampleWithNewData);
      });

      it('should return NewProfessor for empty Professor initial value', () => {
        const formGroup = service.createProfessorFormGroup();

        const professor = service.getProfessor(formGroup) as any;

        expect(professor).toMatchObject({});
      });

      it('should return IProfessor', () => {
        const formGroup = service.createProfessorFormGroup(sampleWithRequiredData);

        const professor = service.getProfessor(formGroup) as any;

        expect(professor).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProfessor should not enable id FormControl', () => {
        const formGroup = service.createProfessorFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProfessor should disable id FormControl', () => {
        const formGroup = service.createProfessorFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
