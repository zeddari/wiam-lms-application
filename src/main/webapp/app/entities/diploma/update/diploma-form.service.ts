import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDiploma, NewDiploma } from '../diploma.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDiploma for edit and NewDiplomaFormGroupInput for create.
 */
type DiplomaFormGroupInput = IDiploma | PartialWithRequiredKeyOf<NewDiploma>;

type DiplomaFormDefaults = Pick<NewDiploma, 'id'>;

type DiplomaFormGroupContent = {
  id: FormControl<IDiploma['id'] | NewDiploma['id']>;
  title: FormControl<IDiploma['title']>;
  subject: FormControl<IDiploma['subject']>;
  detail: FormControl<IDiploma['detail']>;
  supervisor: FormControl<IDiploma['supervisor']>;
  grade: FormControl<IDiploma['grade']>;
  graduationDate: FormControl<IDiploma['graduationDate']>;
  school: FormControl<IDiploma['school']>;
  userCustom: FormControl<IDiploma['userCustom']>;
  diplomaType: FormControl<IDiploma['diplomaType']>;
};

export type DiplomaFormGroup = FormGroup<DiplomaFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DiplomaFormService {
  createDiplomaFormGroup(diploma: DiplomaFormGroupInput = { id: null }): DiplomaFormGroup {
    const diplomaRawValue = {
      ...this.getFormDefaults(),
      ...diploma,
    };
    return new FormGroup<DiplomaFormGroupContent>({
      id: new FormControl(
        { value: diplomaRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(diplomaRawValue.title, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      subject: new FormControl(diplomaRawValue.subject, {
        validators: [Validators.maxLength(100)],
      }),
      detail: new FormControl(diplomaRawValue.detail, {
        validators: [Validators.maxLength(500)],
      }),
      supervisor: new FormControl(diplomaRawValue.supervisor, {
        validators: [Validators.maxLength(500)],
      }),
      grade: new FormControl(diplomaRawValue.grade),
      graduationDate: new FormControl(diplomaRawValue.graduationDate),
      school: new FormControl(diplomaRawValue.school),
      userCustom: new FormControl(diplomaRawValue.userCustom),
      diplomaType: new FormControl(diplomaRawValue.diplomaType),
    });
  }

  getDiploma(form: DiplomaFormGroup): IDiploma | NewDiploma {
    return form.getRawValue() as IDiploma | NewDiploma;
  }

  resetForm(form: DiplomaFormGroup, diploma: DiplomaFormGroupInput): void {
    const diplomaRawValue = { ...this.getFormDefaults(), ...diploma };
    form.reset(
      {
        ...diplomaRawValue,
        id: { value: diplomaRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DiplomaFormDefaults {
    return {
      id: null,
    };
  }
}
