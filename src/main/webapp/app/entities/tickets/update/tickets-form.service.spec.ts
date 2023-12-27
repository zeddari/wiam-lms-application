import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../tickets.test-samples';

import { TicketsFormService } from './tickets-form.service';

describe('Tickets Form Service', () => {
  let service: TicketsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TicketsFormService);
  });

  describe('Service methods', () => {
    describe('createTicketsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTicketsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            justifDoc: expect.any(Object),
            dateTicket: expect.any(Object),
            dateProcess: expect.any(Object),
            processed: expect.any(Object),
            userCustom: expect.any(Object),
            subject: expect.any(Object),
          }),
        );
      });

      it('passing ITickets should create a new form with FormGroup', () => {
        const formGroup = service.createTicketsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            justifDoc: expect.any(Object),
            dateTicket: expect.any(Object),
            dateProcess: expect.any(Object),
            processed: expect.any(Object),
            userCustom: expect.any(Object),
            subject: expect.any(Object),
          }),
        );
      });
    });

    describe('getTickets', () => {
      it('should return NewTickets for default Tickets initial value', () => {
        const formGroup = service.createTicketsFormGroup(sampleWithNewData);

        const tickets = service.getTickets(formGroup) as any;

        expect(tickets).toMatchObject(sampleWithNewData);
      });

      it('should return NewTickets for empty Tickets initial value', () => {
        const formGroup = service.createTicketsFormGroup();

        const tickets = service.getTickets(formGroup) as any;

        expect(tickets).toMatchObject({});
      });

      it('should return ITickets', () => {
        const formGroup = service.createTicketsFormGroup(sampleWithRequiredData);

        const tickets = service.getTickets(formGroup) as any;

        expect(tickets).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITickets should not enable id FormControl', () => {
        const formGroup = service.createTicketsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTickets should disable id FormControl', () => {
        const formGroup = service.createTicketsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
