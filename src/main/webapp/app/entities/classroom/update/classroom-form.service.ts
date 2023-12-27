import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IClassroom, NewClassroom } from '../classroom.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClassroom for edit and NewClassroomFormGroupInput for create.
 */
type ClassroomFormGroupInput = IClassroom | PartialWithRequiredKeyOf<NewClassroom>;

type ClassroomFormDefaults = Pick<NewClassroom, 'id'>;

type ClassroomFormGroupContent = {
  id: FormControl<IClassroom['id'] | NewClassroom['id']>;
  nameAr: FormControl<IClassroom['nameAr']>;
  nameLat: FormControl<IClassroom['nameLat']>;
  description: FormControl<IClassroom['description']>;
  site: FormControl<IClassroom['site']>;
};

export type ClassroomFormGroup = FormGroup<ClassroomFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClassroomFormService {
  createClassroomFormGroup(classroom: ClassroomFormGroupInput = { id: null }): ClassroomFormGroup {
    const classroomRawValue = {
      ...this.getFormDefaults(),
      ...classroom,
    };
    return new FormGroup<ClassroomFormGroupContent>({
      id: new FormControl(
        { value: classroomRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nameAr: new FormControl(classroomRawValue.nameAr, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      nameLat: new FormControl(classroomRawValue.nameLat, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(classroomRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      site: new FormControl(classroomRawValue.site),
    });
  }

  getClassroom(form: ClassroomFormGroup): IClassroom | NewClassroom {
    return form.getRawValue() as IClassroom | NewClassroom;
  }

  resetForm(form: ClassroomFormGroup, classroom: ClassroomFormGroupInput): void {
    const classroomRawValue = { ...this.getFormDefaults(), ...classroom };
    form.reset(
      {
        ...classroomRawValue,
        id: { value: classroomRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ClassroomFormDefaults {
    return {
      id: null,
    };
  }
}
