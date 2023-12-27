import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../job-title.test-samples';

import { JobTitleFormService } from './job-title-form.service';

describe('JobTitle Form Service', () => {
  let service: JobTitleFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(JobTitleFormService);
  });

  describe('Service methods', () => {
    describe('createJobTitleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createJobTitleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing IJobTitle should create a new form with FormGroup', () => {
        const formGroup = service.createJobTitleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getJobTitle', () => {
      it('should return NewJobTitle for default JobTitle initial value', () => {
        const formGroup = service.createJobTitleFormGroup(sampleWithNewData);

        const jobTitle = service.getJobTitle(formGroup) as any;

        expect(jobTitle).toMatchObject(sampleWithNewData);
      });

      it('should return NewJobTitle for empty JobTitle initial value', () => {
        const formGroup = service.createJobTitleFormGroup();

        const jobTitle = service.getJobTitle(formGroup) as any;

        expect(jobTitle).toMatchObject({});
      });

      it('should return IJobTitle', () => {
        const formGroup = service.createJobTitleFormGroup(sampleWithRequiredData);

        const jobTitle = service.getJobTitle(formGroup) as any;

        expect(jobTitle).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IJobTitle should not enable id FormControl', () => {
        const formGroup = service.createJobTitleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewJobTitle should disable id FormControl', () => {
        const formGroup = service.createJobTitleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
