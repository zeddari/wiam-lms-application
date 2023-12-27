import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../follow-up.test-samples';

import { FollowUpFormService } from './follow-up-form.service';

describe('FollowUp Form Service', () => {
  let service: FollowUpFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FollowUpFormService);
  });

  describe('Service methods', () => {
    describe('createFollowUpFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFollowUpFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fromSourate: expect.any(Object),
            fromAya: expect.any(Object),
            toSourate: expect.any(Object),
            toAya: expect.any(Object),
            tilawaType: expect.any(Object),
            notation: expect.any(Object),
            remarks: expect.any(Object),
            coteryHistory: expect.any(Object),
          }),
        );
      });

      it('passing IFollowUp should create a new form with FormGroup', () => {
        const formGroup = service.createFollowUpFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fromSourate: expect.any(Object),
            fromAya: expect.any(Object),
            toSourate: expect.any(Object),
            toAya: expect.any(Object),
            tilawaType: expect.any(Object),
            notation: expect.any(Object),
            remarks: expect.any(Object),
            coteryHistory: expect.any(Object),
          }),
        );
      });
    });

    describe('getFollowUp', () => {
      it('should return NewFollowUp for default FollowUp initial value', () => {
        const formGroup = service.createFollowUpFormGroup(sampleWithNewData);

        const followUp = service.getFollowUp(formGroup) as any;

        expect(followUp).toMatchObject(sampleWithNewData);
      });

      it('should return NewFollowUp for empty FollowUp initial value', () => {
        const formGroup = service.createFollowUpFormGroup();

        const followUp = service.getFollowUp(formGroup) as any;

        expect(followUp).toMatchObject({});
      });

      it('should return IFollowUp', () => {
        const formGroup = service.createFollowUpFormGroup(sampleWithRequiredData);

        const followUp = service.getFollowUp(formGroup) as any;

        expect(followUp).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFollowUp should not enable id FormControl', () => {
        const formGroup = service.createFollowUpFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFollowUp should disable id FormControl', () => {
        const formGroup = service.createFollowUpFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
