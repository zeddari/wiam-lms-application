import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../session-join-mode.test-samples';

import { SessionJoinModeFormService } from './session-join-mode-form.service';

describe('SessionJoinMode Form Service', () => {
  let service: SessionJoinModeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionJoinModeFormService);
  });

  describe('Service methods', () => {
    describe('createSessionJoinModeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSessionJoinModeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });

      it('passing ISessionJoinMode should create a new form with FormGroup', () => {
        const formGroup = service.createSessionJoinModeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
          }),
        );
      });
    });

    describe('getSessionJoinMode', () => {
      it('should return NewSessionJoinMode for default SessionJoinMode initial value', () => {
        const formGroup = service.createSessionJoinModeFormGroup(sampleWithNewData);

        const sessionJoinMode = service.getSessionJoinMode(formGroup) as any;

        expect(sessionJoinMode).toMatchObject(sampleWithNewData);
      });

      it('should return NewSessionJoinMode for empty SessionJoinMode initial value', () => {
        const formGroup = service.createSessionJoinModeFormGroup();

        const sessionJoinMode = service.getSessionJoinMode(formGroup) as any;

        expect(sessionJoinMode).toMatchObject({});
      });

      it('should return ISessionJoinMode', () => {
        const formGroup = service.createSessionJoinModeFormGroup(sampleWithRequiredData);

        const sessionJoinMode = service.getSessionJoinMode(formGroup) as any;

        expect(sessionJoinMode).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISessionJoinMode should not enable id FormControl', () => {
        const formGroup = service.createSessionJoinModeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSessionJoinMode should disable id FormControl', () => {
        const formGroup = service.createSessionJoinModeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
