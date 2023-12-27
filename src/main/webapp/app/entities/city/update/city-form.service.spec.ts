import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../city.test-samples';

import { CityFormService } from './city-form.service';

describe('City Form Service', () => {
  let service: CityFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CityFormService);
  });

  describe('Service methods', () => {
    describe('createCityFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCityFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nameAr: expect.any(Object),
            nameLat: expect.any(Object),
          }),
        );
      });

      it('passing ICity should create a new form with FormGroup', () => {
        const formGroup = service.createCityFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nameAr: expect.any(Object),
            nameLat: expect.any(Object),
          }),
        );
      });
    });

    describe('getCity', () => {
      it('should return NewCity for default City initial value', () => {
        const formGroup = service.createCityFormGroup(sampleWithNewData);

        const city = service.getCity(formGroup) as any;

        expect(city).toMatchObject(sampleWithNewData);
      });

      it('should return NewCity for empty City initial value', () => {
        const formGroup = service.createCityFormGroup();

        const city = service.getCity(formGroup) as any;

        expect(city).toMatchObject({});
      });

      it('should return ICity', () => {
        const formGroup = service.createCityFormGroup(sampleWithRequiredData);

        const city = service.getCity(formGroup) as any;

        expect(city).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICity should not enable id FormControl', () => {
        const formGroup = service.createCityFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCity should disable id FormControl', () => {
        const formGroup = service.createCityFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
