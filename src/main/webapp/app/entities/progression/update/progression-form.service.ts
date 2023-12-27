import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProgression, NewProgression } from '../progression.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProgression for edit and NewProgressionFormGroupInput for create.
 */
type ProgressionFormGroupInput = IProgression | PartialWithRequiredKeyOf<NewProgression>;

type ProgressionFormDefaults = Pick<NewProgression, 'id' | 'status' | 'isJustified' | 'taskDone'>;

type ProgressionFormGroupContent = {
  id: FormControl<IProgression['id'] | NewProgression['id']>;
  status: FormControl<IProgression['status']>;
  isJustified: FormControl<IProgression['isJustified']>;
  justifRef: FormControl<IProgression['justifRef']>;
  lateArrival: FormControl<IProgression['lateArrival']>;
  earlyDeparture: FormControl<IProgression['earlyDeparture']>;
  taskDone: FormControl<IProgression['taskDone']>;
  grade1: FormControl<IProgression['grade1']>;
  description: FormControl<IProgression['description']>;
  taskStart: FormControl<IProgression['taskStart']>;
  taskEnd: FormControl<IProgression['taskEnd']>;
  taskStep: FormControl<IProgression['taskStep']>;
  progressionDate: FormControl<IProgression['progressionDate']>;
  session: FormControl<IProgression['session']>;
  student1: FormControl<IProgression['student1']>;
  mode: FormControl<IProgression['mode']>;
};

export type ProgressionFormGroup = FormGroup<ProgressionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProgressionFormService {
  createProgressionFormGroup(progression: ProgressionFormGroupInput = { id: null }): ProgressionFormGroup {
    const progressionRawValue = {
      ...this.getFormDefaults(),
      ...progression,
    };
    return new FormGroup<ProgressionFormGroupContent>({
      id: new FormControl(
        { value: progressionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      status: new FormControl(progressionRawValue.status, {
        validators: [Validators.required],
      }),
      isJustified: new FormControl(progressionRawValue.isJustified),
      justifRef: new FormControl(progressionRawValue.justifRef),
      lateArrival: new FormControl(progressionRawValue.lateArrival, {
        validators: [Validators.min(0)],
      }),
      earlyDeparture: new FormControl(progressionRawValue.earlyDeparture, {
        validators: [Validators.min(0)],
      }),
      taskDone: new FormControl(progressionRawValue.taskDone, {
        validators: [Validators.required],
      }),
      grade1: new FormControl(progressionRawValue.grade1),
      description: new FormControl(progressionRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      taskStart: new FormControl(progressionRawValue.taskStart, {
        validators: [Validators.min(1), Validators.max(480)],
      }),
      taskEnd: new FormControl(progressionRawValue.taskEnd, {
        validators: [Validators.min(1), Validators.max(480)],
      }),
      taskStep: new FormControl(progressionRawValue.taskStep),
      progressionDate: new FormControl(progressionRawValue.progressionDate, {
        validators: [Validators.required],
      }),
      session: new FormControl(progressionRawValue.session),
      student1: new FormControl(progressionRawValue.student1),
      mode: new FormControl(progressionRawValue.mode),
    });
  }

  getProgression(form: ProgressionFormGroup): IProgression | NewProgression {
    return form.getRawValue() as IProgression | NewProgression;
  }

  resetForm(form: ProgressionFormGroup, progression: ProgressionFormGroupInput): void {
    const progressionRawValue = { ...this.getFormDefaults(), ...progression };
    form.reset(
      {
        ...progressionRawValue,
        id: { value: progressionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProgressionFormDefaults {
    return {
      id: null,
      status: false,
      isJustified: false,
      taskDone: false,
    };
  }
}
