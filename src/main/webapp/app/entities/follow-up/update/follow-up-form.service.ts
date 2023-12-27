import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFollowUp, NewFollowUp } from '../follow-up.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFollowUp for edit and NewFollowUpFormGroupInput for create.
 */
type FollowUpFormGroupInput = IFollowUp | PartialWithRequiredKeyOf<NewFollowUp>;

type FollowUpFormDefaults = Pick<NewFollowUp, 'id'>;

type FollowUpFormGroupContent = {
  id: FormControl<IFollowUp['id'] | NewFollowUp['id']>;
  fromSourate: FormControl<IFollowUp['fromSourate']>;
  fromAya: FormControl<IFollowUp['fromAya']>;
  toSourate: FormControl<IFollowUp['toSourate']>;
  toAya: FormControl<IFollowUp['toAya']>;
  tilawaType: FormControl<IFollowUp['tilawaType']>;
  notation: FormControl<IFollowUp['notation']>;
  remarks: FormControl<IFollowUp['remarks']>;
  coteryHistory: FormControl<IFollowUp['coteryHistory']>;
};

export type FollowUpFormGroup = FormGroup<FollowUpFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FollowUpFormService {
  createFollowUpFormGroup(followUp: FollowUpFormGroupInput = { id: null }): FollowUpFormGroup {
    const followUpRawValue = {
      ...this.getFormDefaults(),
      ...followUp,
    };
    return new FormGroup<FollowUpFormGroupContent>({
      id: new FormControl(
        { value: followUpRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fromSourate: new FormControl(followUpRawValue.fromSourate),
      fromAya: new FormControl(followUpRawValue.fromAya),
      toSourate: new FormControl(followUpRawValue.toSourate),
      toAya: new FormControl(followUpRawValue.toAya),
      tilawaType: new FormControl(followUpRawValue.tilawaType),
      notation: new FormControl(followUpRawValue.notation),
      remarks: new FormControl(followUpRawValue.remarks),
      coteryHistory: new FormControl(followUpRawValue.coteryHistory),
    });
  }

  getFollowUp(form: FollowUpFormGroup): IFollowUp | NewFollowUp {
    return form.getRawValue() as IFollowUp | NewFollowUp;
  }

  resetForm(form: FollowUpFormGroup, followUp: FollowUpFormGroupInput): void {
    const followUpRawValue = { ...this.getFormDefaults(), ...followUp };
    form.reset(
      {
        ...followUpRawValue,
        id: { value: followUpRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): FollowUpFormDefaults {
    return {
      id: null,
    };
  }
}
