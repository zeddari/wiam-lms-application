import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICotery, NewCotery } from '../cotery.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICotery for edit and NewCoteryFormGroupInput for create.
 */
type CoteryFormGroupInput = ICotery | PartialWithRequiredKeyOf<NewCotery>;

type CoteryFormDefaults = Pick<NewCotery, 'id'>;

type CoteryFormGroupContent = {
  id: FormControl<ICotery['id'] | NewCotery['id']>;
  date: FormControl<ICotery['date']>;
  coteryName: FormControl<ICotery['coteryName']>;
  studentFullName: FormControl<ICotery['studentFullName']>;
  attendanceStatus: FormControl<ICotery['attendanceStatus']>;
};

export type CoteryFormGroup = FormGroup<CoteryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CoteryFormService {
  createCoteryFormGroup(cotery: CoteryFormGroupInput = { id: null }): CoteryFormGroup {
    const coteryRawValue = {
      ...this.getFormDefaults(),
      ...cotery,
    };
    return new FormGroup<CoteryFormGroupContent>({
      id: new FormControl(
        { value: coteryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(coteryRawValue.date),
      coteryName: new FormControl(coteryRawValue.coteryName),
      studentFullName: new FormControl(coteryRawValue.studentFullName),
      attendanceStatus: new FormControl(coteryRawValue.attendanceStatus),
    });
  }

  getCotery(form: CoteryFormGroup): ICotery | NewCotery {
    return form.getRawValue() as ICotery | NewCotery;
  }

  resetForm(form: CoteryFormGroup, cotery: CoteryFormGroupInput): void {
    const coteryRawValue = { ...this.getFormDefaults(), ...cotery };
    form.reset(
      {
        ...coteryRawValue,
        id: { value: coteryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CoteryFormDefaults {
    return {
      id: null,
    };
  }
}
