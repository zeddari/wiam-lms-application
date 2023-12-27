import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISessionProvider, NewSessionProvider } from '../session-provider.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISessionProvider for edit and NewSessionProviderFormGroupInput for create.
 */
type SessionProviderFormGroupInput = ISessionProvider | PartialWithRequiredKeyOf<NewSessionProvider>;

type SessionProviderFormDefaults = Pick<NewSessionProvider, 'id'>;

type SessionProviderFormGroupContent = {
  id: FormControl<ISessionProvider['id'] | NewSessionProvider['id']>;
  name: FormControl<ISessionProvider['name']>;
  description: FormControl<ISessionProvider['description']>;
  website: FormControl<ISessionProvider['website']>;
};

export type SessionProviderFormGroup = FormGroup<SessionProviderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SessionProviderFormService {
  createSessionProviderFormGroup(sessionProvider: SessionProviderFormGroupInput = { id: null }): SessionProviderFormGroup {
    const sessionProviderRawValue = {
      ...this.getFormDefaults(),
      ...sessionProvider,
    };
    return new FormGroup<SessionProviderFormGroupContent>({
      id: new FormControl(
        { value: sessionProviderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(sessionProviderRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(sessionProviderRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      website: new FormControl(sessionProviderRawValue.website, {
        validators: [Validators.maxLength(500)],
      }),
    });
  }

  getSessionProvider(form: SessionProviderFormGroup): ISessionProvider | NewSessionProvider {
    return form.getRawValue() as ISessionProvider | NewSessionProvider;
  }

  resetForm(form: SessionProviderFormGroup, sessionProvider: SessionProviderFormGroupInput): void {
    const sessionProviderRawValue = { ...this.getFormDefaults(), ...sessionProvider };
    form.reset(
      {
        ...sessionProviderRawValue,
        id: { value: sessionProviderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SessionProviderFormDefaults {
    return {
      id: null,
    };
  }
}
