import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISite, NewSite } from '../site.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISite for edit and NewSiteFormGroupInput for create.
 */
type SiteFormGroupInput = ISite | PartialWithRequiredKeyOf<NewSite>;

type SiteFormDefaults = Pick<NewSite, 'id'>;

type SiteFormGroupContent = {
  id: FormControl<ISite['id'] | NewSite['id']>;
  nameAr: FormControl<ISite['nameAr']>;
  nameLat: FormControl<ISite['nameLat']>;
  description: FormControl<ISite['description']>;
  localisation: FormControl<ISite['localisation']>;
  city: FormControl<ISite['city']>;
};

export type SiteFormGroup = FormGroup<SiteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SiteFormService {
  createSiteFormGroup(site: SiteFormGroupInput = { id: null }): SiteFormGroup {
    const siteRawValue = {
      ...this.getFormDefaults(),
      ...site,
    };
    return new FormGroup<SiteFormGroupContent>({
      id: new FormControl(
        { value: siteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nameAr: new FormControl(siteRawValue.nameAr, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      nameLat: new FormControl(siteRawValue.nameLat, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(siteRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      localisation: new FormControl(siteRawValue.localisation),
      city: new FormControl(siteRawValue.city),
    });
  }

  getSite(form: SiteFormGroup): ISite | NewSite {
    return form.getRawValue() as ISite | NewSite;
  }

  resetForm(form: SiteFormGroup, site: SiteFormGroupInput): void {
    const siteRawValue = { ...this.getFormDefaults(), ...site };
    form.reset(
      {
        ...siteRawValue,
        id: { value: siteRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SiteFormDefaults {
    return {
      id: null,
    };
  }
}
