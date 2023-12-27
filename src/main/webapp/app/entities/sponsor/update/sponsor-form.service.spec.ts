import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../sponsor.test-samples';

import { SponsorFormService } from './sponsor-form.service';

describe('Sponsor Form Service', () => {
  let service: SponsorFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SponsorFormService);
  });

  describe('Service methods', () => {
    describe('createSponsorFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSponsorFormGroup();

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
            students: expect.any(Object),
          }),
        );
      });

      it('passing ISponsor should create a new form with FormGroup', () => {
        const formGroup = service.createSponsorFormGroup(sampleWithRequiredData);

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
            students: expect.any(Object),
          }),
        );
      });
    });

    describe('getSponsor', () => {
      it('should return NewSponsor for default Sponsor initial value', () => {
        const formGroup = service.createSponsorFormGroup(sampleWithNewData);

        const sponsor = service.getSponsor(formGroup) as any;

        expect(sponsor).toMatchObject(sampleWithNewData);
      });

      it('should return NewSponsor for empty Sponsor initial value', () => {
        const formGroup = service.createSponsorFormGroup();

        const sponsor = service.getSponsor(formGroup) as any;

        expect(sponsor).toMatchObject({});
      });

      it('should return ISponsor', () => {
        const formGroup = service.createSponsorFormGroup(sampleWithRequiredData);

        const sponsor = service.getSponsor(formGroup) as any;

        expect(sponsor).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISponsor should not enable id FormControl', () => {
        const formGroup = service.createSponsorFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSponsor should disable id FormControl', () => {
        const formGroup = service.createSponsorFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
