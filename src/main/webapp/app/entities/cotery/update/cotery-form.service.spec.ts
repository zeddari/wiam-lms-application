import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../cotery.test-samples';

import { CoteryFormService } from './cotery-form.service';

describe('Cotery Form Service', () => {
  let service: CoteryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CoteryFormService);
  });

  describe('Service methods', () => {
    describe('createCoteryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCoteryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            coteryName: expect.any(Object),
            studentFullName: expect.any(Object),
            attendanceStatus: expect.any(Object),
          }),
        );
      });

      it('passing ICotery should create a new form with FormGroup', () => {
        const formGroup = service.createCoteryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            coteryName: expect.any(Object),
            studentFullName: expect.any(Object),
            attendanceStatus: expect.any(Object),
          }),
        );
      });
    });

    describe('getCotery', () => {
      it('should return NewCotery for default Cotery initial value', () => {
        const formGroup = service.createCoteryFormGroup(sampleWithNewData);

        const cotery = service.getCotery(formGroup) as any;

        expect(cotery).toMatchObject(sampleWithNewData);
      });

      it('should return NewCotery for empty Cotery initial value', () => {
        const formGroup = service.createCoteryFormGroup();

        const cotery = service.getCotery(formGroup) as any;

        expect(cotery).toMatchObject({});
      });

      it('should return ICotery', () => {
        const formGroup = service.createCoteryFormGroup(sampleWithRequiredData);

        const cotery = service.getCotery(formGroup) as any;

        expect(cotery).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICotery should not enable id FormControl', () => {
        const formGroup = service.createCoteryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCotery should disable id FormControl', () => {
        const formGroup = service.createCoteryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
