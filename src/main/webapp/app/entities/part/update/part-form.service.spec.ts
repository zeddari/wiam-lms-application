import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../part.test-samples';

import { PartFormService } from './part-form.service';

describe('Part Form Service', () => {
  let service: PartFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PartFormService);
  });

  describe('Service methods', () => {
    describe('createPartFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPartFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
            description: expect.any(Object),
            duration: expect.any(Object),
            imageLink: expect.any(Object),
            videoLink: expect.any(Object),
            course: expect.any(Object),
            part1: expect.any(Object),
          }),
        );
      });

      it('passing IPart should create a new form with FormGroup', () => {
        const formGroup = service.createPartFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            titleAr: expect.any(Object),
            titleLat: expect.any(Object),
            description: expect.any(Object),
            duration: expect.any(Object),
            imageLink: expect.any(Object),
            videoLink: expect.any(Object),
            course: expect.any(Object),
            part1: expect.any(Object),
          }),
        );
      });
    });

    describe('getPart', () => {
      it('should return NewPart for default Part initial value', () => {
        const formGroup = service.createPartFormGroup(sampleWithNewData);

        const part = service.getPart(formGroup) as any;

        expect(part).toMatchObject(sampleWithNewData);
      });

      it('should return NewPart for empty Part initial value', () => {
        const formGroup = service.createPartFormGroup();

        const part = service.getPart(formGroup) as any;

        expect(part).toMatchObject({});
      });

      it('should return IPart', () => {
        const formGroup = service.createPartFormGroup(sampleWithRequiredData);

        const part = service.getPart(formGroup) as any;

        expect(part).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPart should not enable id FormControl', () => {
        const formGroup = service.createPartFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPart should disable id FormControl', () => {
        const formGroup = service.createPartFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
