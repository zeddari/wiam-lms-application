import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../session-link.test-samples';

import { SessionLinkFormService } from './session-link-form.service';

describe('SessionLink Form Service', () => {
  let service: SessionLinkFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionLinkFormService);
  });

  describe('Service methods', () => {
    describe('createSessionLinkFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSessionLinkFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            link: expect.any(Object),
            provider: expect.any(Object),
          }),
        );
      });

      it('passing ISessionLink should create a new form with FormGroup', () => {
        const formGroup = service.createSessionLinkFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            description: expect.any(Object),
            link: expect.any(Object),
            provider: expect.any(Object),
          }),
        );
      });
    });

    describe('getSessionLink', () => {
      it('should return NewSessionLink for default SessionLink initial value', () => {
        const formGroup = service.createSessionLinkFormGroup(sampleWithNewData);

        const sessionLink = service.getSessionLink(formGroup) as any;

        expect(sessionLink).toMatchObject(sampleWithNewData);
      });

      it('should return NewSessionLink for empty SessionLink initial value', () => {
        const formGroup = service.createSessionLinkFormGroup();

        const sessionLink = service.getSessionLink(formGroup) as any;

        expect(sessionLink).toMatchObject({});
      });

      it('should return ISessionLink', () => {
        const formGroup = service.createSessionLinkFormGroup(sampleWithRequiredData);

        const sessionLink = service.getSessionLink(formGroup) as any;

        expect(sessionLink).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISessionLink should not enable id FormControl', () => {
        const formGroup = service.createSessionLinkFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSessionLink should disable id FormControl', () => {
        const formGroup = service.createSessionLinkFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
