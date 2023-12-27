import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../user-custom.test-samples';

import { UserCustomFormService } from './user-custom-form.service';

describe('UserCustom Form Service', () => {
  let service: UserCustomFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserCustomFormService);
  });

  describe('Service methods', () => {
    describe('createUserCustomFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createUserCustomFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            email: expect.any(Object),
            accountName: expect.any(Object),
            role: expect.any(Object),
            status: expect.any(Object),
            password: expect.any(Object),
            phoneNumber1: expect.any(Object),
            phoneNumver2: expect.any(Object),
            sex: expect.any(Object),
            countryInternalId: expect.any(Object),
            nationalityId: expect.any(Object),
            birthDay: expect.any(Object),
            photo: expect.any(Object),
            address: expect.any(Object),
            facebook: expect.any(Object),
            telegramUserCustomId: expect.any(Object),
            telegramUserCustomName: expect.any(Object),
            biography: expect.any(Object),
            bankAccountDetails: expect.any(Object),
            certificate: expect.any(Object),
            jobInternalId: expect.any(Object),
            creationDate: expect.any(Object),
            modificationDate: expect.any(Object),
            deletionDate: expect.any(Object),
            country: expect.any(Object),
            job: expect.any(Object),
            exams: expect.any(Object),
          }),
        );
      });

      it('passing IUserCustom should create a new form with FormGroup', () => {
        const formGroup = service.createUserCustomFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            firstName: expect.any(Object),
            lastName: expect.any(Object),
            email: expect.any(Object),
            accountName: expect.any(Object),
            role: expect.any(Object),
            status: expect.any(Object),
            password: expect.any(Object),
            phoneNumber1: expect.any(Object),
            phoneNumver2: expect.any(Object),
            sex: expect.any(Object),
            countryInternalId: expect.any(Object),
            nationalityId: expect.any(Object),
            birthDay: expect.any(Object),
            photo: expect.any(Object),
            address: expect.any(Object),
            facebook: expect.any(Object),
            telegramUserCustomId: expect.any(Object),
            telegramUserCustomName: expect.any(Object),
            biography: expect.any(Object),
            bankAccountDetails: expect.any(Object),
            certificate: expect.any(Object),
            jobInternalId: expect.any(Object),
            creationDate: expect.any(Object),
            modificationDate: expect.any(Object),
            deletionDate: expect.any(Object),
            country: expect.any(Object),
            job: expect.any(Object),
            exams: expect.any(Object),
          }),
        );
      });
    });

    describe('getUserCustom', () => {
      it('should return NewUserCustom for default UserCustom initial value', () => {
        const formGroup = service.createUserCustomFormGroup(sampleWithNewData);

        const userCustom = service.getUserCustom(formGroup) as any;

        expect(userCustom).toMatchObject(sampleWithNewData);
      });

      it('should return NewUserCustom for empty UserCustom initial value', () => {
        const formGroup = service.createUserCustomFormGroup();

        const userCustom = service.getUserCustom(formGroup) as any;

        expect(userCustom).toMatchObject({});
      });

      it('should return IUserCustom', () => {
        const formGroup = service.createUserCustomFormGroup(sampleWithRequiredData);

        const userCustom = service.getUserCustom(formGroup) as any;

        expect(userCustom).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IUserCustom should not enable id FormControl', () => {
        const formGroup = service.createUserCustomFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewUserCustom should disable id FormControl', () => {
        const formGroup = service.createUserCustomFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
