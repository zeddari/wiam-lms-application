import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../session-provider.test-samples';

import { SessionProviderFormService } from './session-provider-form.service';

describe('SessionProvider Form Service', () => {
  let service: SessionProviderFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionProviderFormService);
  });

  describe('Service methods', () => {
    describe('createSessionProviderFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSessionProviderFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            website: expect.any(Object),
          }),
        );
      });

      it('passing ISessionProvider should create a new form with FormGroup', () => {
        const formGroup = service.createSessionProviderFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            website: expect.any(Object),
          }),
        );
      });
    });

    describe('getSessionProvider', () => {
      it('should return NewSessionProvider for default SessionProvider initial value', () => {
        const formGroup = service.createSessionProviderFormGroup(sampleWithNewData);

        const sessionProvider = service.getSessionProvider(formGroup) as any;

        expect(sessionProvider).toMatchObject(sampleWithNewData);
      });

      it('should return NewSessionProvider for empty SessionProvider initial value', () => {
        const formGroup = service.createSessionProviderFormGroup();

        const sessionProvider = service.getSessionProvider(formGroup) as any;

        expect(sessionProvider).toMatchObject({});
      });

      it('should return ISessionProvider', () => {
        const formGroup = service.createSessionProviderFormGroup(sampleWithRequiredData);

        const sessionProvider = service.getSessionProvider(formGroup) as any;

        expect(sessionProvider).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISessionProvider should not enable id FormControl', () => {
        const formGroup = service.createSessionProviderFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSessionProvider should disable id FormControl', () => {
        const formGroup = service.createSessionProviderFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
