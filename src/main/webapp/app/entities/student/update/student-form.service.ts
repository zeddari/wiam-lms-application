import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IStudent, NewStudent } from '../student.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IStudent for edit and NewStudentFormGroupInput for create.
 */
type StudentFormGroupInput = IStudent | PartialWithRequiredKeyOf<NewStudent>;

type StudentFormDefaults = Pick<NewStudent, 'id' | 'gender'>;

type StudentFormGroupContent = {
  id: FormControl<IStudent['id'] | NewStudent['id']>;
  phoneNumber: FormControl<IStudent['phoneNumber']>;
  mobileNumber: FormControl<IStudent['mobileNumber']>;
  gender: FormControl<IStudent['gender']>;
  about: FormControl<IStudent['about']>;
  imageLink: FormControl<IStudent['imageLink']>;
  imageLinkContentType: FormControl<IStudent['imageLinkContentType']>;
  code: FormControl<IStudent['code']>;
  birthdate: FormControl<IStudent['birthdate']>;
  lastDegree: FormControl<IStudent['lastDegree']>;
  userCustom: FormControl<IStudent['userCustom']>;
  group2: FormControl<IStudent['group2']>;
  country: FormControl<IStudent['country']>;
};

export type StudentFormGroup = FormGroup<StudentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class StudentFormService {
  createStudentFormGroup(student: StudentFormGroupInput = { id: null }): StudentFormGroup {
    const studentRawValue = {
      ...this.getFormDefaults(),
      ...student,
    };
    return new FormGroup<StudentFormGroupContent>({
      id: new FormControl(
        { value: studentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      phoneNumber: new FormControl(studentRawValue.phoneNumber),
      mobileNumber: new FormControl(studentRawValue.mobileNumber),
      gender: new FormControl(studentRawValue.gender),
      about: new FormControl(studentRawValue.about, {
        validators: [Validators.maxLength(500)],
      }),
      imageLink: new FormControl(studentRawValue.imageLink),
      imageLinkContentType: new FormControl(studentRawValue.imageLinkContentType),
      code: new FormControl(studentRawValue.code, {
        validators: [Validators.maxLength(100)],
      }),
      birthdate: new FormControl(studentRawValue.birthdate),
      lastDegree: new FormControl(studentRawValue.lastDegree, {
        validators: [Validators.maxLength(500)],
      }),
      userCustom: new FormControl(studentRawValue.userCustom),
      group2: new FormControl(studentRawValue.group2),
      country: new FormControl(studentRawValue.country),
    });
  }

  getStudent(form: StudentFormGroup): IStudent | NewStudent {
    return form.getRawValue() as IStudent | NewStudent;
  }

  resetForm(form: StudentFormGroup, student: StudentFormGroupInput): void {
    const studentRawValue = { ...this.getFormDefaults(), ...student };
    form.reset(
      {
        ...studentRawValue,
        id: { value: studentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): StudentFormDefaults {
    return {
      id: null,
      gender: false,
    };
  }
}
