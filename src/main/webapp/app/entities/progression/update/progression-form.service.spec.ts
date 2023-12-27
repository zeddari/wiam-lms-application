import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../progression.test-samples';

import { ProgressionFormService } from './progression-form.service';

describe('Progression Form Service', () => {
  let service: ProgressionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgressionFormService);
  });

  describe('Service methods', () => {
    describe('createProgressionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProgressionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            status: expect.any(Object),
            isJustified: expect.any(Object),
            justifRef: expect.any(Object),
            lateArrival: expect.any(Object),
            earlyDeparture: expect.any(Object),
            taskDone: expect.any(Object),
            grade1: expect.any(Object),
            description: expect.any(Object),
            taskStart: expect.any(Object),
            taskEnd: expect.any(Object),
            taskStep: expect.any(Object),
            progressionDate: expect.any(Object),
            session: expect.any(Object),
            student1: expect.any(Object),
            mode: expect.any(Object),
          }),
        );
      });

      it('passing IProgression should create a new form with FormGroup', () => {
        const formGroup = service.createProgressionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            status: expect.any(Object),
            isJustified: expect.any(Object),
            justifRef: expect.any(Object),
            lateArrival: expect.any(Object),
            earlyDeparture: expect.any(Object),
            taskDone: expect.any(Object),
            grade1: expect.any(Object),
            description: expect.any(Object),
            taskStart: expect.any(Object),
            taskEnd: expect.any(Object),
            taskStep: expect.any(Object),
            progressionDate: expect.any(Object),
            session: expect.any(Object),
            student1: expect.any(Object),
            mode: expect.any(Object),
          }),
        );
      });
    });

    describe('getProgression', () => {
      it('should return NewProgression for default Progression initial value', () => {
        const formGroup = service.createProgressionFormGroup(sampleWithNewData);

        const progression = service.getProgression(formGroup) as any;

        expect(progression).toMatchObject(sampleWithNewData);
      });

      it('should return NewProgression for empty Progression initial value', () => {
        const formGroup = service.createProgressionFormGroup();

        const progression = service.getProgression(formGroup) as any;

        expect(progression).toMatchObject({});
      });

      it('should return IProgression', () => {
        const formGroup = service.createProgressionFormGroup(sampleWithRequiredData);

        const progression = service.getProgression(formGroup) as any;

        expect(progression).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProgression should not enable id FormControl', () => {
        const formGroup = service.createProgressionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProgression should disable id FormControl', () => {
        const formGroup = service.createProgressionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
