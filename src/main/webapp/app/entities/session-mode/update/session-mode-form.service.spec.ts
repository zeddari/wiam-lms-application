import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../session-mode.test-samples';

import { SessionModeFormService } from './session-mode-form.service';

describe('SessionMode Form Service', () => {
  let service: SessionModeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionModeFormService);
  });

  describe('Service methods', () => {
    describe('createSessionModeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSessionModeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing ISessionMode should create a new form with FormGroup', () => {
        const formGroup = service.createSessionModeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getSessionMode', () => {
      it('should return NewSessionMode for default SessionMode initial value', () => {
        const formGroup = service.createSessionModeFormGroup(sampleWithNewData);

        const sessionMode = service.getSessionMode(formGroup) as any;

        expect(sessionMode).toMatchObject(sampleWithNewData);
      });

      it('should return NewSessionMode for empty SessionMode initial value', () => {
        const formGroup = service.createSessionModeFormGroup();

        const sessionMode = service.getSessionMode(formGroup) as any;

        expect(sessionMode).toMatchObject({});
      });

      it('should return ISessionMode', () => {
        const formGroup = service.createSessionModeFormGroup(sampleWithRequiredData);

        const sessionMode = service.getSessionMode(formGroup) as any;

        expect(sessionMode).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISessionMode should not enable id FormControl', () => {
        const formGroup = service.createSessionModeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSessionMode should disable id FormControl', () => {
        const formGroup = service.createSessionModeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
