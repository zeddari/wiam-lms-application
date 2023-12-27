import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICountry2, NewCountry2 } from '../country-2.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICountry2 for edit and NewCountry2FormGroupInput for create.
 */
type Country2FormGroupInput = ICountry2 | PartialWithRequiredKeyOf<NewCountry2>;

type Country2FormDefaults = Pick<NewCountry2, 'id'>;

type Country2FormGroupContent = {
  id: FormControl<ICountry2['id'] | NewCountry2['id']>;
  countryName: FormControl<ICountry2['countryName']>;
};

export type Country2FormGroup = FormGroup<Country2FormGroupContent>;

@Injectable({ providedIn: 'root' })
export class Country2FormService {
  createCountry2FormGroup(country2: Country2FormGroupInput = { id: null }): Country2FormGroup {
    const country2RawValue = {
      ...this.getFormDefaults(),
      ...country2,
    };
    return new FormGroup<Country2FormGroupContent>({
      id: new FormControl(
        { value: country2RawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      countryName: new FormControl(country2RawValue.countryName, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
    });
  }

  getCountry2(form: Country2FormGroup): ICountry2 | NewCountry2 {
    return form.getRawValue() as ICountry2 | NewCountry2;
  }

  resetForm(form: Country2FormGroup, country2: Country2FormGroupInput): void {
    const country2RawValue = { ...this.getFormDefaults(), ...country2 };
    form.reset(
      {
        ...country2RawValue,
        id: { value: country2RawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): Country2FormDefaults {
    return {
      id: null,
    };
  }
}
