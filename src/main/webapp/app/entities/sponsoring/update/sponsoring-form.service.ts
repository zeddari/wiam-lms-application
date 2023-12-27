import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISponsoring, NewSponsoring } from '../sponsoring.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISponsoring for edit and NewSponsoringFormGroupInput for create.
 */
type SponsoringFormGroupInput = ISponsoring | PartialWithRequiredKeyOf<NewSponsoring>;

type SponsoringFormDefaults = Pick<NewSponsoring, 'id' | 'isAlways'>;

type SponsoringFormGroupContent = {
  id: FormControl<ISponsoring['id'] | NewSponsoring['id']>;
  message: FormControl<ISponsoring['message']>;
  amount: FormControl<ISponsoring['amount']>;
  startDate: FormControl<ISponsoring['startDate']>;
  endDate: FormControl<ISponsoring['endDate']>;
  isAlways: FormControl<ISponsoring['isAlways']>;
  sponsor: FormControl<ISponsoring['sponsor']>;
  project: FormControl<ISponsoring['project']>;
  currency: FormControl<ISponsoring['currency']>;
};

export type SponsoringFormGroup = FormGroup<SponsoringFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SponsoringFormService {
  createSponsoringFormGroup(sponsoring: SponsoringFormGroupInput = { id: null }): SponsoringFormGroup {
    const sponsoringRawValue = {
      ...this.getFormDefaults(),
      ...sponsoring,
    };
    return new FormGroup<SponsoringFormGroupContent>({
      id: new FormControl(
        { value: sponsoringRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      message: new FormControl(sponsoringRawValue.message, {
        validators: [Validators.maxLength(500)],
      }),
      amount: new FormControl(sponsoringRawValue.amount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      startDate: new FormControl(sponsoringRawValue.startDate),
      endDate: new FormControl(sponsoringRawValue.endDate),
      isAlways: new FormControl(sponsoringRawValue.isAlways),
      sponsor: new FormControl(sponsoringRawValue.sponsor),
      project: new FormControl(sponsoringRawValue.project),
      currency: new FormControl(sponsoringRawValue.currency),
    });
  }

  getSponsoring(form: SponsoringFormGroup): ISponsoring | NewSponsoring {
    return form.getRawValue() as ISponsoring | NewSponsoring;
  }

  resetForm(form: SponsoringFormGroup, sponsoring: SponsoringFormGroupInput): void {
    const sponsoringRawValue = { ...this.getFormDefaults(), ...sponsoring };
    form.reset(
      {
        ...sponsoringRawValue,
        id: { value: sponsoringRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SponsoringFormDefaults {
    return {
      id: null,
      isAlways: false,
    };
  }
}
