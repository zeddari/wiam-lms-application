import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../sponsoring.test-samples';

import { SponsoringFormService } from './sponsoring-form.service';

describe('Sponsoring Form Service', () => {
  let service: SponsoringFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SponsoringFormService);
  });

  describe('Service methods', () => {
    describe('createSponsoringFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSponsoringFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            message: expect.any(Object),
            amount: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            isAlways: expect.any(Object),
            sponsor: expect.any(Object),
            project: expect.any(Object),
            currency: expect.any(Object),
          }),
        );
      });

      it('passing ISponsoring should create a new form with FormGroup', () => {
        const formGroup = service.createSponsoringFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            message: expect.any(Object),
            amount: expect.any(Object),
            startDate: expect.any(Object),
            endDate: expect.any(Object),
            isAlways: expect.any(Object),
            sponsor: expect.any(Object),
            project: expect.any(Object),
            currency: expect.any(Object),
          }),
        );
      });
    });

    describe('getSponsoring', () => {
      it('should return NewSponsoring for default Sponsoring initial value', () => {
        const formGroup = service.createSponsoringFormGroup(sampleWithNewData);

        const sponsoring = service.getSponsoring(formGroup) as any;

        expect(sponsoring).toMatchObject(sampleWithNewData);
      });

      it('should return NewSponsoring for empty Sponsoring initial value', () => {
        const formGroup = service.createSponsoringFormGroup();

        const sponsoring = service.getSponsoring(formGroup) as any;

        expect(sponsoring).toMatchObject({});
      });

      it('should return ISponsoring', () => {
        const formGroup = service.createSponsoringFormGroup(sampleWithRequiredData);

        const sponsoring = service.getSponsoring(formGroup) as any;

        expect(sponsoring).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISponsoring should not enable id FormControl', () => {
        const formGroup = service.createSponsoringFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSponsoring should disable id FormControl', () => {
        const formGroup = service.createSponsoringFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
