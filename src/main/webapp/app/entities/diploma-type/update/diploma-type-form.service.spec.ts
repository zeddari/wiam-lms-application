import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../diploma-type.test-samples';

import { DiplomaTypeFormService } from './diploma-type-form.service';

describe('DiplomaType Form Service', () => {
  let service: DiplomaTypeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DiplomaTypeFormService);
  });

  describe('Service methods', () => {
    describe('createDiplomaTypeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDiplomaTypeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
          }),
        );
      });

      it('passing IDiplomaType should create a new form with FormGroup', () => {
        const formGroup = service.createDiplomaTypeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
          }),
        );
      });
    });

    describe('getDiplomaType', () => {
      it('should return NewDiplomaType for default DiplomaType initial value', () => {
        const formGroup = service.createDiplomaTypeFormGroup(sampleWithNewData);

        const diplomaType = service.getDiplomaType(formGroup) as any;

        expect(diplomaType).toMatchObject(sampleWithNewData);
      });

      it('should return NewDiplomaType for empty DiplomaType initial value', () => {
        const formGroup = service.createDiplomaTypeFormGroup();

        const diplomaType = service.getDiplomaType(formGroup) as any;

        expect(diplomaType).toMatchObject({});
      });

      it('should return IDiplomaType', () => {
        const formGroup = service.createDiplomaTypeFormGroup(sampleWithRequiredData);

        const diplomaType = service.getDiplomaType(formGroup) as any;

        expect(diplomaType).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDiplomaType should not enable id FormControl', () => {
        const formGroup = service.createDiplomaTypeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDiplomaType should disable id FormControl', () => {
        const formGroup = service.createDiplomaTypeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
