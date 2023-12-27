import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../progression-mode.test-samples';

import { ProgressionModeFormService } from './progression-mode-form.service';

describe('ProgressionMode Form Service', () => {
  let service: ProgressionModeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgressionModeFormService);
  });

  describe('Service methods', () => {
    describe('createProgressionModeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgressionModeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
          }),
        );
      });

      it('passing IProgressionMode should create a new form with FormGroup', () => {
        const formGroup = service.createProgressionModeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
          }),
        );
      });
    });

    describe('getProgressionMode', () => {
      it('should return NewProgressionMode for default ProgressionMode initial value', () => {
        const formGroup = service.createProgressionModeFormGroup(sampleWithNewData);

        const progressionMode = service.getProgressionMode(formGroup) as any;

        expect(progressionMode).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgressionMode for empty ProgressionMode initial value', () => {
        const formGroup = service.createProgressionModeFormGroup();

        const progressionMode = service.getProgressionMode(formGroup) as any;

        expect(progressionMode).toMatchObject({});
      });

      it('should return IProgressionMode', () => {
        const formGroup = service.createProgressionModeFormGroup(sampleWithRequiredData);

        const progressionMode = service.getProgressionMode(formGroup) as any;

        expect(progressionMode).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgressionMode should not enable id FormControl', () => {
        const formGroup = service.createProgressionModeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgressionMode should disable id FormControl', () => {
        const formGroup = service.createProgressionModeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
