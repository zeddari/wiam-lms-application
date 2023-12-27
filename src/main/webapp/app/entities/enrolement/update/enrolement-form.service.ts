import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEnrolement, NewEnrolement } from '../enrolement.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEnrolement for edit and NewEnrolementFormGroupInput for create.
 */
type EnrolementFormGroupInput = IEnrolement | PartialWithRequiredKeyOf<NewEnrolement>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IEnrolement | NewEnrolement> = Omit<
  T,
  'activatedAt' | 'activatedBy' | 'enrolmentStartTime' | 'enrolemntEndTime'
> & {
  activatedAt?: string | null;
  activatedBy?: string | null;
  enrolmentStartTime?: string | null;
  enrolemntEndTime?: string | null;
};

type EnrolementFormRawValue = FormValueOf<IEnrolement>;

type NewEnrolementFormRawValue = FormValueOf<NewEnrolement>;

type EnrolementFormDefaults = Pick<
  NewEnrolement,
  'id' | 'isActive' | 'activatedAt' | 'activatedBy' | 'enrolmentStartTime' | 'enrolemntEndTime'
>;

type EnrolementFormGroupContent = {
  id: FormControl<EnrolementFormRawValue['id'] | NewEnrolement['id']>;
  isActive: FormControl<EnrolementFormRawValue['isActive']>;
  activatedAt: FormControl<EnrolementFormRawValue['activatedAt']>;
  activatedBy: FormControl<EnrolementFormRawValue['activatedBy']>;
  enrolmentStartTime: FormControl<EnrolementFormRawValue['enrolmentStartTime']>;
  enrolemntEndTime: FormControl<EnrolementFormRawValue['enrolemntEndTime']>;
  student: FormControl<EnrolementFormRawValue['student']>;
  course: FormControl<EnrolementFormRawValue['course']>;
};

export type EnrolementFormGroup = FormGroup<EnrolementFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EnrolementFormService {
  createEnrolementFormGroup(enrolement: EnrolementFormGroupInput = { id: null }): EnrolementFormGroup {
    const enrolementRawValue = this.convertEnrolementToEnrolementRawValue({
      ...this.getFormDefaults(),
      ...enrolement,
    });
    return new FormGroup<EnrolementFormGroupContent>({
      id: new FormControl(
        { value: enrolementRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      isActive: new FormControl(enrolementRawValue.isActive, {
        validators: [Validators.required],
      }),
      activatedAt: new FormControl(enrolementRawValue.activatedAt),
      activatedBy: new FormControl(enrolementRawValue.activatedBy),
      enrolmentStartTime: new FormControl(enrolementRawValue.enrolmentStartTime, {
        validators: [Validators.required],
      }),
      enrolemntEndTime: new FormControl(enrolementRawValue.enrolemntEndTime, {
        validators: [Validators.required],
      }),
      student: new FormControl(enrolementRawValue.student),
      course: new FormControl(enrolementRawValue.course),
    });
  }

  getEnrolement(form: EnrolementFormGroup): IEnrolement | NewEnrolement {
    return this.convertEnrolementRawValueToEnrolement(form.getRawValue() as EnrolementFormRawValue | NewEnrolementFormRawValue);
  }

  resetForm(form: EnrolementFormGroup, enrolement: EnrolementFormGroupInput): void {
    const enrolementRawValue = this.convertEnrolementToEnrolementRawValue({ ...this.getFormDefaults(), ...enrolement });
    form.reset(
      {
        ...enrolementRawValue,
        id: { value: enrolementRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EnrolementFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isActive: false,
      activatedAt: currentTime,
      activatedBy: currentTime,
      enrolmentStartTime: currentTime,
      enrolemntEndTime: currentTime,
    };
  }

  private convertEnrolementRawValueToEnrolement(
    rawEnrolement: EnrolementFormRawValue | NewEnrolementFormRawValue,
  ): IEnrolement | NewEnrolement {
    return {
      ...rawEnrolement,
      activatedAt: dayjs(rawEnrolement.activatedAt, DATE_TIME_FORMAT),
      activatedBy: dayjs(rawEnrolement.activatedBy, DATE_TIME_FORMAT),
      enrolmentStartTime: dayjs(rawEnrolement.enrolmentStartTime, DATE_TIME_FORMAT),
      enrolemntEndTime: dayjs(rawEnrolement.enrolemntEndTime, DATE_TIME_FORMAT),
    };
  }

  private convertEnrolementToEnrolementRawValue(
    enrolement: IEnrolement | (Partial<NewEnrolement> & EnrolementFormDefaults),
  ): EnrolementFormRawValue | PartialWithRequiredKeyOf<NewEnrolementFormRawValue> {
    return {
      ...enrolement,
      activatedAt: enrolement.activatedAt ? enrolement.activatedAt.format(DATE_TIME_FORMAT) : undefined,
      activatedBy: enrolement.activatedBy ? enrolement.activatedBy.format(DATE_TIME_FORMAT) : undefined,
      enrolmentStartTime: enrolement.enrolmentStartTime ? enrolement.enrolmentStartTime.format(DATE_TIME_FORMAT) : undefined,
      enrolemntEndTime: enrolement.enrolemntEndTime ? enrolement.enrolemntEndTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
