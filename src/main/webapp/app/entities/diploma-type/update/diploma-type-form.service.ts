import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IDiplomaType, NewDiplomaType } from '../diploma-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDiplomaType for edit and NewDiplomaTypeFormGroupInput for create.
 */
type DiplomaTypeFormGroupInput = IDiplomaType | PartialWithRequiredKeyOf<NewDiplomaType>;

type DiplomaTypeFormDefaults = Pick<NewDiplomaType, 'id'>;

type DiplomaTypeFormGroupContent = {
  id: FormControl<IDiplomaType['id'] | NewDiplomaType['id']>;
  titleAr: FormControl<IDiplomaType['titleAr']>;
  titleLat: FormControl<IDiplomaType['titleLat']>;
};

export type DiplomaTypeFormGroup = FormGroup<DiplomaTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DiplomaTypeFormService {
  createDiplomaTypeFormGroup(diplomaType: DiplomaTypeFormGroupInput = { id: null }): DiplomaTypeFormGroup {
    const diplomaTypeRawValue = {
      ...this.getFormDefaults(),
      ...diplomaType,
    };
    return new FormGroup<DiplomaTypeFormGroupContent>({
      id: new FormControl(
        { value: diplomaTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titleAr: new FormControl(diplomaTypeRawValue.titleAr, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      titleLat: new FormControl(diplomaTypeRawValue.titleLat, {
        validators: [Validators.maxLength(20)],
      }),
    });
  }

  getDiplomaType(form: DiplomaTypeFormGroup): IDiplomaType | NewDiplomaType {
    return form.getRawValue() as IDiplomaType | NewDiplomaType;
  }

  resetForm(form: DiplomaTypeFormGroup, diplomaType: DiplomaTypeFormGroupInput): void {
    const diplomaTypeRawValue = { ...this.getFormDefaults(), ...diplomaType };
    form.reset(
      {
        ...diplomaTypeRawValue,
        id: { value: diplomaTypeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DiplomaTypeFormDefaults {
    return {
      id: null,
    };
  }
}
