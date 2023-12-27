import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISponsor, NewSponsor } from '../sponsor.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISponsor for edit and NewSponsorFormGroupInput for create.
 */
type SponsorFormGroupInput = ISponsor | PartialWithRequiredKeyOf<NewSponsor>;

type SponsorFormDefaults = Pick<NewSponsor, 'id' | 'gender' | 'students'>;

type SponsorFormGroupContent = {
  id: FormControl<ISponsor['id'] | NewSponsor['id']>;
  phoneNumber: FormControl<ISponsor['phoneNumber']>;
  mobileNumber: FormControl<ISponsor['mobileNumber']>;
  gender: FormControl<ISponsor['gender']>;
  about: FormControl<ISponsor['about']>;
  imageLink: FormControl<ISponsor['imageLink']>;
  imageLinkContentType: FormControl<ISponsor['imageLinkContentType']>;
  code: FormControl<ISponsor['code']>;
  birthdate: FormControl<ISponsor['birthdate']>;
  lastDegree: FormControl<ISponsor['lastDegree']>;
  userCustom: FormControl<ISponsor['userCustom']>;
  students: FormControl<ISponsor['students']>;
};

export type SponsorFormGroup = FormGroup<SponsorFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SponsorFormService {
  createSponsorFormGroup(sponsor: SponsorFormGroupInput = { id: null }): SponsorFormGroup {
    const sponsorRawValue = {
      ...this.getFormDefaults(),
      ...sponsor,
    };
    return new FormGroup<SponsorFormGroupContent>({
      id: new FormControl(
        { value: sponsorRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      phoneNumber: new FormControl(sponsorRawValue.phoneNumber),
      mobileNumber: new FormControl(sponsorRawValue.mobileNumber),
      gender: new FormControl(sponsorRawValue.gender),
      about: new FormControl(sponsorRawValue.about, {
        validators: [Validators.maxLength(500)],
      }),
      imageLink: new FormControl(sponsorRawValue.imageLink),
      imageLinkContentType: new FormControl(sponsorRawValue.imageLinkContentType),
      code: new FormControl(sponsorRawValue.code, {
        validators: [Validators.maxLength(100)],
      }),
      birthdate: new FormControl(sponsorRawValue.birthdate),
      lastDegree: new FormControl(sponsorRawValue.lastDegree, {
        validators: [Validators.maxLength(500)],
      }),
      userCustom: new FormControl(sponsorRawValue.userCustom),
      students: new FormControl(sponsorRawValue.students ?? []),
    });
  }

  getSponsor(form: SponsorFormGroup): ISponsor | NewSponsor {
    return form.getRawValue() as ISponsor | NewSponsor;
  }

  resetForm(form: SponsorFormGroup, sponsor: SponsorFormGroupInput): void {
    const sponsorRawValue = { ...this.getFormDefaults(), ...sponsor };
    form.reset(
      {
        ...sponsorRawValue,
        id: { value: sponsorRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SponsorFormDefaults {
    return {
      id: null,
      gender: false,
      students: [],
    };
  }
}
