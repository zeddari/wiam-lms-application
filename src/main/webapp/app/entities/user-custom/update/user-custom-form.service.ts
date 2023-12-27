import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IUserCustom, NewUserCustom } from '../user-custom.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IUserCustom for edit and NewUserCustomFormGroupInput for create.
 */
type UserCustomFormGroupInput = IUserCustom | PartialWithRequiredKeyOf<NewUserCustom>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IUserCustom | NewUserCustom> = Omit<T, 'creationDate' | 'modificationDate' | 'deletionDate'> & {
  creationDate?: string | null;
  modificationDate?: string | null;
  deletionDate?: string | null;
};

type UserCustomFormRawValue = FormValueOf<IUserCustom>;

type NewUserCustomFormRawValue = FormValueOf<NewUserCustom>;

type UserCustomFormDefaults = Pick<NewUserCustom, 'id' | 'creationDate' | 'modificationDate' | 'deletionDate' | 'exams'>;

type UserCustomFormGroupContent = {
  id: FormControl<UserCustomFormRawValue['id'] | NewUserCustom['id']>;
  firstName: FormControl<UserCustomFormRawValue['firstName']>;
  lastName: FormControl<UserCustomFormRawValue['lastName']>;
  email: FormControl<UserCustomFormRawValue['email']>;
  accountName: FormControl<UserCustomFormRawValue['accountName']>;
  role: FormControl<UserCustomFormRawValue['role']>;
  status: FormControl<UserCustomFormRawValue['status']>;
  password: FormControl<UserCustomFormRawValue['password']>;
  phoneNumber1: FormControl<UserCustomFormRawValue['phoneNumber1']>;
  phoneNumver2: FormControl<UserCustomFormRawValue['phoneNumver2']>;
  sex: FormControl<UserCustomFormRawValue['sex']>;
  countryInternalId: FormControl<UserCustomFormRawValue['countryInternalId']>;
  nationalityId: FormControl<UserCustomFormRawValue['nationalityId']>;
  birthDay: FormControl<UserCustomFormRawValue['birthDay']>;
  photo: FormControl<UserCustomFormRawValue['photo']>;
  photoContentType: FormControl<UserCustomFormRawValue['photoContentType']>;
  address: FormControl<UserCustomFormRawValue['address']>;
  facebook: FormControl<UserCustomFormRawValue['facebook']>;
  telegramUserCustomId: FormControl<UserCustomFormRawValue['telegramUserCustomId']>;
  telegramUserCustomName: FormControl<UserCustomFormRawValue['telegramUserCustomName']>;
  biography: FormControl<UserCustomFormRawValue['biography']>;
  bankAccountDetails: FormControl<UserCustomFormRawValue['bankAccountDetails']>;
  certificate: FormControl<UserCustomFormRawValue['certificate']>;
  certificateContentType: FormControl<UserCustomFormRawValue['certificateContentType']>;
  jobInternalId: FormControl<UserCustomFormRawValue['jobInternalId']>;
  creationDate: FormControl<UserCustomFormRawValue['creationDate']>;
  modificationDate: FormControl<UserCustomFormRawValue['modificationDate']>;
  deletionDate: FormControl<UserCustomFormRawValue['deletionDate']>;
  country: FormControl<UserCustomFormRawValue['country']>;
  job: FormControl<UserCustomFormRawValue['job']>;
  exams: FormControl<UserCustomFormRawValue['exams']>;
};

export type UserCustomFormGroup = FormGroup<UserCustomFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class UserCustomFormService {
  createUserCustomFormGroup(userCustom: UserCustomFormGroupInput = { id: null }): UserCustomFormGroup {
    const userCustomRawValue = this.convertUserCustomToUserCustomRawValue({
      ...this.getFormDefaults(),
      ...userCustom,
    });
    return new FormGroup<UserCustomFormGroupContent>({
      id: new FormControl(
        { value: userCustomRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      firstName: new FormControl(userCustomRawValue.firstName, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      lastName: new FormControl(userCustomRawValue.lastName, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      email: new FormControl(userCustomRawValue.email, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      accountName: new FormControl(userCustomRawValue.accountName, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      role: new FormControl(userCustomRawValue.role, {
        validators: [Validators.required],
      }),
      status: new FormControl(userCustomRawValue.status, {
        validators: [Validators.required],
      }),
      password: new FormControl(userCustomRawValue.password, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      phoneNumber1: new FormControl(userCustomRawValue.phoneNumber1, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
      phoneNumver2: new FormControl(userCustomRawValue.phoneNumver2, {
        validators: [Validators.maxLength(50)],
      }),
      sex: new FormControl(userCustomRawValue.sex, {
        validators: [Validators.required],
      }),
      countryInternalId: new FormControl(userCustomRawValue.countryInternalId, {
        validators: [Validators.required],
      }),
      nationalityId: new FormControl(userCustomRawValue.nationalityId, {
        validators: [Validators.required],
      }),
      birthDay: new FormControl(userCustomRawValue.birthDay, {
        validators: [Validators.required],
      }),
      photo: new FormControl(userCustomRawValue.photo),
      photoContentType: new FormControl(userCustomRawValue.photoContentType),
      address: new FormControl(userCustomRawValue.address),
      facebook: new FormControl(userCustomRawValue.facebook),
      telegramUserCustomId: new FormControl(userCustomRawValue.telegramUserCustomId),
      telegramUserCustomName: new FormControl(userCustomRawValue.telegramUserCustomName),
      biography: new FormControl(userCustomRawValue.biography),
      bankAccountDetails: new FormControl(userCustomRawValue.bankAccountDetails),
      certificate: new FormControl(userCustomRawValue.certificate),
      certificateContentType: new FormControl(userCustomRawValue.certificateContentType),
      jobInternalId: new FormControl(userCustomRawValue.jobInternalId),
      creationDate: new FormControl(userCustomRawValue.creationDate, {
        validators: [Validators.required],
      }),
      modificationDate: new FormControl(userCustomRawValue.modificationDate),
      deletionDate: new FormControl(userCustomRawValue.deletionDate),
      country: new FormControl(userCustomRawValue.country),
      job: new FormControl(userCustomRawValue.job),
      exams: new FormControl(userCustomRawValue.exams ?? []),
    });
  }

  getUserCustom(form: UserCustomFormGroup): IUserCustom | NewUserCustom {
    return this.convertUserCustomRawValueToUserCustom(form.getRawValue() as UserCustomFormRawValue | NewUserCustomFormRawValue);
  }

  resetForm(form: UserCustomFormGroup, userCustom: UserCustomFormGroupInput): void {
    const userCustomRawValue = this.convertUserCustomToUserCustomRawValue({ ...this.getFormDefaults(), ...userCustom });
    form.reset(
      {
        ...userCustomRawValue,
        id: { value: userCustomRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): UserCustomFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      creationDate: currentTime,
      modificationDate: currentTime,
      deletionDate: currentTime,
      exams: [],
    };
  }

  private convertUserCustomRawValueToUserCustom(
    rawUserCustom: UserCustomFormRawValue | NewUserCustomFormRawValue,
  ): IUserCustom | NewUserCustom {
    return {
      ...rawUserCustom,
      creationDate: dayjs(rawUserCustom.creationDate, DATE_TIME_FORMAT),
      modificationDate: dayjs(rawUserCustom.modificationDate, DATE_TIME_FORMAT),
      deletionDate: dayjs(rawUserCustom.deletionDate, DATE_TIME_FORMAT),
    };
  }

  private convertUserCustomToUserCustomRawValue(
    userCustom: IUserCustom | (Partial<NewUserCustom> & UserCustomFormDefaults),
  ): UserCustomFormRawValue | PartialWithRequiredKeyOf<NewUserCustomFormRawValue> {
    return {
      ...userCustom,
      creationDate: userCustom.creationDate ? userCustom.creationDate.format(DATE_TIME_FORMAT) : undefined,
      modificationDate: userCustom.modificationDate ? userCustom.modificationDate.format(DATE_TIME_FORMAT) : undefined,
      deletionDate: userCustom.deletionDate ? userCustom.deletionDate.format(DATE_TIME_FORMAT) : undefined,
      exams: userCustom.exams ?? [],
    };
  }
}
