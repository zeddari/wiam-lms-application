import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISessionMode, NewSessionMode } from '../session-mode.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISessionMode for edit and NewSessionModeFormGroupInput for create.
 */
type SessionModeFormGroupInput = ISessionMode | PartialWithRequiredKeyOf<NewSessionMode>;

type SessionModeFormDefaults = Pick<NewSessionMode, 'id'>;

type SessionModeFormGroupContent = {
  id: FormControl<ISessionMode['id'] | NewSessionMode['id']>;
  title: FormControl<ISessionMode['title']>;
  description: FormControl<ISessionMode['description']>;
};

export type SessionModeFormGroup = FormGroup<SessionModeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SessionModeFormService {
  createSessionModeFormGroup(sessionMode: SessionModeFormGroupInput = { id: null }): SessionModeFormGroup {
    const sessionModeRawValue = {
      ...this.getFormDefaults(),
      ...sessionMode,
    };
    return new FormGroup<SessionModeFormGroupContent>({
      id: new FormControl(
        { value: sessionModeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(sessionModeRawValue.title, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(sessionModeRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
    });
  }

  getSessionMode(form: SessionModeFormGroup): ISessionMode | NewSessionMode {
    return form.getRawValue() as ISessionMode | NewSessionMode;
  }

  resetForm(form: SessionModeFormGroup, sessionMode: SessionModeFormGroupInput): void {
    const sessionModeRawValue = { ...this.getFormDefaults(), ...sessionMode };
    form.reset(
      {
        ...sessionModeRawValue,
        id: { value: sessionModeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SessionModeFormDefaults {
    return {
      id: null,
    };
  }
}
