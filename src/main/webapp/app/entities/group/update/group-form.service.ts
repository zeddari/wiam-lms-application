import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IGroup, NewGroup } from '../group.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGroup for edit and NewGroupFormGroupInput for create.
 */
type GroupFormGroupInput = IGroup | PartialWithRequiredKeyOf<NewGroup>;

type GroupFormDefaults = Pick<NewGroup, 'id'>;

type GroupFormGroupContent = {
  id: FormControl<IGroup['id'] | NewGroup['id']>;
  nameAr: FormControl<IGroup['nameAr']>;
  nameLat: FormControl<IGroup['nameLat']>;
  description: FormControl<IGroup['description']>;
  group1: FormControl<IGroup['group1']>;
};

export type GroupFormGroup = FormGroup<GroupFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GroupFormService {
  createGroupFormGroup(group: GroupFormGroupInput = { id: null }): GroupFormGroup {
    const groupRawValue = {
      ...this.getFormDefaults(),
      ...group,
    };
    return new FormGroup<GroupFormGroupContent>({
      id: new FormControl(
        { value: groupRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nameAr: new FormControl(groupRawValue.nameAr, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      nameLat: new FormControl(groupRawValue.nameLat, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(groupRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      group1: new FormControl(groupRawValue.group1),
    });
  }

  getGroup(form: GroupFormGroup): IGroup | NewGroup {
    return form.getRawValue() as IGroup | NewGroup;
  }

  resetForm(form: GroupFormGroup, group: GroupFormGroupInput): void {
    const groupRawValue = { ...this.getFormDefaults(), ...group };
    form.reset(
      {
        ...groupRawValue,
        id: { value: groupRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): GroupFormDefaults {
    return {
      id: null,
    };
  }
}
