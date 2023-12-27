import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISessionJoinMode, NewSessionJoinMode } from '../session-join-mode.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISessionJoinMode for edit and NewSessionJoinModeFormGroupInput for create.
 */
type SessionJoinModeFormGroupInput = ISessionJoinMode | PartialWithRequiredKeyOf<NewSessionJoinMode>;

type SessionJoinModeFormDefaults = Pick<NewSessionJoinMode, 'id'>;

type SessionJoinModeFormGroupContent = {
  id: FormControl<ISessionJoinMode['id'] | NewSessionJoinMode['id']>;
  title: FormControl<ISessionJoinMode['title']>;
  description: FormControl<ISessionJoinMode['description']>;
};

export type SessionJoinModeFormGroup = FormGroup<SessionJoinModeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SessionJoinModeFormService {
  createSessionJoinModeFormGroup(sessionJoinMode: SessionJoinModeFormGroupInput = { id: null }): SessionJoinModeFormGroup {
    const sessionJoinModeRawValue = {
      ...this.getFormDefaults(),
      ...sessionJoinMode,
    };
    return new FormGroup<SessionJoinModeFormGroupContent>({
      id: new FormControl(
        { value: sessionJoinModeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(sessionJoinModeRawValue.title, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(sessionJoinModeRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
    });
  }

  getSessionJoinMode(form: SessionJoinModeFormGroup): ISessionJoinMode | NewSessionJoinMode {
    return form.getRawValue() as ISessionJoinMode | NewSessionJoinMode;
  }

  resetForm(form: SessionJoinModeFormGroup, sessionJoinMode: SessionJoinModeFormGroupInput): void {
    const sessionJoinModeRawValue = { ...this.getFormDefaults(), ...sessionJoinMode };
    form.reset(
      {
        ...sessionJoinModeRawValue,
        id: { value: sessionJoinModeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SessionJoinModeFormDefaults {
    return {
      id: null,
    };
  }
}
