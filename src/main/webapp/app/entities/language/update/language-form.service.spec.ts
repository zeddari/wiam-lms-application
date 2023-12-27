import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../language.test-samples';

import { LanguageFormService } from './language-form.service';

describe('Language Form Service', () => {
  let service: LanguageFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LanguageFormService);
  });

  describe('Service methods', () => {
    describe('createLanguageFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLanguageFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            userCustom: expect.any(Object),
          }),
        );
      });

      it('passing ILanguage should create a new form with FormGroup', () => {
        const formGroup = service.createLanguageFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            userCustom: expect.any(Object),
          }),
        );
      });
    });

    describe('getLanguage', () => {
      it('should return NewLanguage for default Language initial value', () => {
        const formGroup = service.createLanguageFormGroup(sampleWithNewData);

        const language = service.getLanguage(formGroup) as any;

        expect(language).toMatchObject(sampleWithNewData);
      });

      it('should return NewLanguage for empty Language initial value', () => {
        const formGroup = service.createLanguageFormGroup();

        const language = service.getLanguage(formGroup) as any;

        expect(language).toMatchObject({});
      });

      it('should return ILanguage', () => {
        const formGroup = service.createLanguageFormGroup(sampleWithRequiredData);

        const language = service.getLanguage(formGroup) as any;

        expect(language).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILanguage should not enable id FormControl', () => {
        const formGroup = service.createLanguageFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLanguage should disable id FormControl', () => {
        const formGroup = service.createLanguageFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
