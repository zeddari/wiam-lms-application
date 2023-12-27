import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../certificate.test-samples';

import { CertificateFormService } from './certificate-form.service';

describe('Certificate Form Service', () => {
  let service: CertificateFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CertificateFormService);
  });

  describe('Service methods', () => {
    describe('createCertificateFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCertificateFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            coteryName: expect.any(Object),
            studentFullName: expect.any(Object),
            certificateType: expect.any(Object),
            student: expect.any(Object),
            cotery: expect.any(Object),
          }),
        );
      });

      it('passing ICertificate should create a new form with FormGroup', () => {
        const formGroup = service.createCertificateFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            coteryName: expect.any(Object),
            studentFullName: expect.any(Object),
            certificateType: expect.any(Object),
            student: expect.any(Object),
            cotery: expect.any(Object),
          }),
        );
      });
    });

    describe('getCertificate', () => {
      it('should return NewCertificate for default Certificate initial value', () => {
        const formGroup = service.createCertificateFormGroup(sampleWithNewData);

        const certificate = service.getCertificate(formGroup) as any;

        expect(certificate).toMatchObject(sampleWithNewData);
      });

      it('should return NewCertificate for empty Certificate initial value', () => {
        const formGroup = service.createCertificateFormGroup();

        const certificate = service.getCertificate(formGroup) as any;

        expect(certificate).toMatchObject({});
      });

      it('should return ICertificate', () => {
        const formGroup = service.createCertificateFormGroup(sampleWithRequiredData);

        const certificate = service.getCertificate(formGroup) as any;

        expect(certificate).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICertificate should not enable id FormControl', () => {
        const formGroup = service.createCertificateFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCertificate should disable id FormControl', () => {
        const formGroup = service.createCertificateFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
