import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProgressionMode, NewProgressionMode } from '../progression-mode.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgressionMode for edit and NewProgressionModeFormGroupInput for create.
 */
type ProgressionModeFormGroupInput = IProgressionMode | PartialWithRequiredKeyOf<NewProgressionMode>;

type ProgressionModeFormDefaults = Pick<NewProgressionMode, 'id'>;

type ProgressionModeFormGroupContent = {
  id: FormControl<IProgressionMode['id'] | NewProgressionMode['id']>;
  titleAr: FormControl<IProgressionMode['titleAr']>;
  titleLat: FormControl<IProgressionMode['titleLat']>;
};

export type ProgressionModeFormGroup = FormGroup<ProgressionModeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgressionModeFormService {
  createProgressionModeFormGroup(progressionMode: ProgressionModeFormGroupInput = { id: null }): ProgressionModeFormGroup {
    const progressionModeRawValue = {
      ...this.getFormDefaults(),
      ...progressionMode,
    };
    return new FormGroup<ProgressionModeFormGroupContent>({
      id: new FormControl(
        { value: progressionModeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titleAr: new FormControl(progressionModeRawValue.titleAr, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      titleLat: new FormControl(progressionModeRawValue.titleLat, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
    });
  }

  getProgressionMode(form: ProgressionModeFormGroup): IProgressionMode | NewProgressionMode {
    return form.getRawValue() as IProgressionMode | NewProgressionMode;
  }

  resetForm(form: ProgressionModeFormGroup, progressionMode: ProgressionModeFormGroupInput): void {
    const progressionModeRawValue = { ...this.getFormDefaults(), ...progressionMode };
    form.reset(
      {
        ...progressionModeRawValue,
        id: { value: progressionModeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProgressionModeFormDefaults {
    return {
      id: null,
    };
  }
}
