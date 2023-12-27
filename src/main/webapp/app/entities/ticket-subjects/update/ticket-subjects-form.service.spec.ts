import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../ticket-subjects.test-samples';

import { TicketSubjectsFormService } from './ticket-subjects-form.service';

describe('TicketSubjects Form Service', () => {
  let service: TicketSubjectsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TicketSubjectsFormService);
  });

  describe('Service methods', () => {
    describe('createTicketSubjectsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTicketSubjectsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing ITicketSubjects should create a new form with FormGroup', () => {
        const formGroup = service.createTicketSubjectsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getTicketSubjects', () => {
      it('should return NewTicketSubjects for default TicketSubjects initial value', () => {
        const formGroup = service.createTicketSubjectsFormGroup(sampleWithNewData);

        const ticketSubjects = service.getTicketSubjects(formGroup) as any;

        expect(ticketSubjects).toMatchObject(sampleWithNewData);
      });

      it('should return NewTicketSubjects for empty TicketSubjects initial value', () => {
        const formGroup = service.createTicketSubjectsFormGroup();

        const ticketSubjects = service.getTicketSubjects(formGroup) as any;

        expect(ticketSubjects).toMatchObject({});
      });

      it('should return ITicketSubjects', () => {
        const formGroup = service.createTicketSubjectsFormGroup(sampleWithRequiredData);

        const ticketSubjects = service.getTicketSubjects(formGroup) as any;

        expect(ticketSubjects).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITicketSubjects should not enable id FormControl', () => {
        const formGroup = service.createTicketSubjectsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTicketSubjects should disable id FormControl', () => {
        const formGroup = service.createTicketSubjectsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
