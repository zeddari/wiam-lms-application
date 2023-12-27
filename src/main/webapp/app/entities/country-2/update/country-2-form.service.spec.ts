import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../country-2.test-samples';

import { Country2FormService } from './country-2-form.service';

describe('Country2 Form Service', () => {
  let service: Country2FormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Country2FormService);
  });

  describe('Service methods', () => {
    describe('createCountry2FormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCountry2FormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            countryName: expect.any(Object),
          }),
        );
      });

      it('passing ICountry2 should create a new form with FormGroup', () => {
        const formGroup = service.createCountry2FormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            countryName: expect.any(Object),
          }),
        );
      });
    });

    describe('getCountry2', () => {
      it('should return NewCountry2 for default Country2 initial value', () => {
        const formGroup = service.createCountry2FormGroup(sampleWithNewData);

        const country2 = service.getCountry2(formGroup) as any;

        expect(country2).toMatchObject(sampleWithNewData);
      });

      it('should return NewCountry2 for empty Country2 initial value', () => {
        const formGroup = service.createCountry2FormGroup();

        const country2 = service.getCountry2(formGroup) as any;

        expect(country2).toMatchObject({});
      });

      it('should return ICountry2', () => {
        const formGroup = service.createCountry2FormGroup(sampleWithRequiredData);

        const country2 = service.getCountry2(formGroup) as any;

        expect(country2).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICountry2 should not enable id FormControl', () => {
        const formGroup = service.createCountry2FormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCountry2 should disable id FormControl', () => {
        const formGroup = service.createCountry2FormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
