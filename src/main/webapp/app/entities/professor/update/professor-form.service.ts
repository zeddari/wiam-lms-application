import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProfessor, NewProfessor } from '../professor.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProfessor for edit and NewProfessorFormGroupInput for create.
 */
type ProfessorFormGroupInput = IProfessor | PartialWithRequiredKeyOf<NewProfessor>;

type ProfessorFormDefaults = Pick<NewProfessor, 'id' | 'gender'>;

type ProfessorFormGroupContent = {
  id: FormControl<IProfessor['id'] | NewProfessor['id']>;
  phoneNumber: FormControl<IProfessor['phoneNumber']>;
  mobileNumber: FormControl<IProfessor['mobileNumber']>;
  gender: FormControl<IProfessor['gender']>;
  about: FormControl<IProfessor['about']>;
  imageLink: FormControl<IProfessor['imageLink']>;
  imageLinkContentType: FormControl<IProfessor['imageLinkContentType']>;
  code: FormControl<IProfessor['code']>;
  birthdate: FormControl<IProfessor['birthdate']>;
  lastDegree: FormControl<IProfessor['lastDegree']>;
  userCustom: FormControl<IProfessor['userCustom']>;
};

export type ProfessorFormGroup = FormGroup<ProfessorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProfessorFormService {
  createProfessorFormGroup(professor: ProfessorFormGroupInput = { id: null }): ProfessorFormGroup {
    const professorRawValue = {
      ...this.getFormDefaults(),
      ...professor,
    };
    return new FormGroup<ProfessorFormGroupContent>({
      id: new FormControl(
        { value: professorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      phoneNumber: new FormControl(professorRawValue.phoneNumber),
      mobileNumber: new FormControl(professorRawValue.mobileNumber),
      gender: new FormControl(professorRawValue.gender),
      about: new FormControl(professorRawValue.about, {
        validators: [Validators.maxLength(500)],
      }),
      imageLink: new FormControl(professorRawValue.imageLink),
      imageLinkContentType: new FormControl(professorRawValue.imageLinkContentType),
      code: new FormControl(professorRawValue.code, {
        validators: [Validators.maxLength(100)],
      }),
      birthdate: new FormControl(professorRawValue.birthdate),
      lastDegree: new FormControl(professorRawValue.lastDegree, {
        validators: [Validators.maxLength(500)],
      }),
      userCustom: new FormControl(professorRawValue.userCustom),
    });
  }

  getProfessor(form: ProfessorFormGroup): IProfessor | NewProfessor {
    return form.getRawValue() as IProfessor | NewProfessor;
  }

  resetForm(form: ProfessorFormGroup, professor: ProfessorFormGroupInput): void {
    const professorRawValue = { ...this.getFormDefaults(), ...professor };
    form.reset(
      {
        ...professorRawValue,
        id: { value: professorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProfessorFormDefaults {
    return {
      id: null,
      gender: false,
    };
  }
}
