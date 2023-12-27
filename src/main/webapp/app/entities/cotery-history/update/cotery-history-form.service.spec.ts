import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../cotery-history.test-samples';

import { CoteryHistoryFormService } from './cotery-history-form.service';

describe('CoteryHistory Form Service', () => {
  let service: CoteryHistoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CoteryHistoryFormService);
  });

  describe('Service methods', () => {
    describe('createCoteryHistoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCoteryHistoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            coteryName: expect.any(Object),
            studentFullName: expect.any(Object),
            attendanceStatus: expect.any(Object),
            student2: expect.any(Object),
            student: expect.any(Object),
          }),
        );
      });

      it('passing ICoteryHistory should create a new form with FormGroup', () => {
        const formGroup = service.createCoteryHistoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            date: expect.any(Object),
            coteryName: expect.any(Object),
            studentFullName: expect.any(Object),
            attendanceStatus: expect.any(Object),
            student2: expect.any(Object),
            student: expect.any(Object),
          }),
        );
      });
    });

    describe('getCoteryHistory', () => {
      it('should return NewCoteryHistory for default CoteryHistory initial value', () => {
        const formGroup = service.createCoteryHistoryFormGroup(sampleWithNewData);

        const coteryHistory = service.getCoteryHistory(formGroup) as any;

        expect(coteryHistory).toMatchObject(sampleWithNewData);
      });

      it('should return NewCoteryHistory for empty CoteryHistory initial value', () => {
        const formGroup = service.createCoteryHistoryFormGroup();

        const coteryHistory = service.getCoteryHistory(formGroup) as any;

        expect(coteryHistory).toMatchObject({});
      });

      it('should return ICoteryHistory', () => {
        const formGroup = service.createCoteryHistoryFormGroup(sampleWithRequiredData);

        const coteryHistory = service.getCoteryHistory(formGroup) as any;

        expect(coteryHistory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICoteryHistory should not enable id FormControl', () => {
        const formGroup = service.createCoteryHistoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCoteryHistory should disable id FormControl', () => {
        const formGroup = service.createCoteryHistoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
