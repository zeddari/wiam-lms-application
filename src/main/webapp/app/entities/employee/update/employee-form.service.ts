import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IEmployee, NewEmployee } from '../employee.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IEmployee for edit and NewEmployeeFormGroupInput for create.
 */
type EmployeeFormGroupInput = IEmployee | PartialWithRequiredKeyOf<NewEmployee>;

type EmployeeFormDefaults = Pick<NewEmployee, 'id' | 'gender'>;

type EmployeeFormGroupContent = {
  id: FormControl<IEmployee['id'] | NewEmployee['id']>;
  phoneNumber: FormControl<IEmployee['phoneNumber']>;
  mobileNumber: FormControl<IEmployee['mobileNumber']>;
  gender: FormControl<IEmployee['gender']>;
  about: FormControl<IEmployee['about']>;
  imageLink: FormControl<IEmployee['imageLink']>;
  imageLinkContentType: FormControl<IEmployee['imageLinkContentType']>;
  code: FormControl<IEmployee['code']>;
  birthdate: FormControl<IEmployee['birthdate']>;
  lastDegree: FormControl<IEmployee['lastDegree']>;
  userCustom: FormControl<IEmployee['userCustom']>;
  departement: FormControl<IEmployee['departement']>;
  job: FormControl<IEmployee['job']>;
};

export type EmployeeFormGroup = FormGroup<EmployeeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class EmployeeFormService {
  createEmployeeFormGroup(employee: EmployeeFormGroupInput = { id: null }): EmployeeFormGroup {
    const employeeRawValue = {
      ...this.getFormDefaults(),
      ...employee,
    };
    return new FormGroup<EmployeeFormGroupContent>({
      id: new FormControl(
        { value: employeeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      phoneNumber: new FormControl(employeeRawValue.phoneNumber),
      mobileNumber: new FormControl(employeeRawValue.mobileNumber),
      gender: new FormControl(employeeRawValue.gender),
      about: new FormControl(employeeRawValue.about, {
        validators: [Validators.maxLength(500)],
      }),
      imageLink: new FormControl(employeeRawValue.imageLink),
      imageLinkContentType: new FormControl(employeeRawValue.imageLinkContentType),
      code: new FormControl(employeeRawValue.code, {
        validators: [Validators.maxLength(100)],
      }),
      birthdate: new FormControl(employeeRawValue.birthdate),
      lastDegree: new FormControl(employeeRawValue.lastDegree, {
        validators: [Validators.maxLength(500)],
      }),
      userCustom: new FormControl(employeeRawValue.userCustom),
      departement: new FormControl(employeeRawValue.departement),
      job: new FormControl(employeeRawValue.job),
    });
  }

  getEmployee(form: EmployeeFormGroup): IEmployee | NewEmployee {
    return form.getRawValue() as IEmployee | NewEmployee;
  }

  resetForm(form: EmployeeFormGroup, employee: EmployeeFormGroupInput): void {
    const employeeRawValue = { ...this.getFormDefaults(), ...employee };
    form.reset(
      {
        ...employeeRawValue,
        id: { value: employeeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): EmployeeFormDefaults {
    return {
      id: null,
      gender: false,
    };
  }
}
