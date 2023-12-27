import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPart, NewPart } from '../part.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPart for edit and NewPartFormGroupInput for create.
 */
type PartFormGroupInput = IPart | PartialWithRequiredKeyOf<NewPart>;

type PartFormDefaults = Pick<NewPart, 'id'>;

type PartFormGroupContent = {
  id: FormControl<IPart['id'] | NewPart['id']>;
  titleAr: FormControl<IPart['titleAr']>;
  titleLat: FormControl<IPart['titleLat']>;
  description: FormControl<IPart['description']>;
  duration: FormControl<IPart['duration']>;
  imageLink: FormControl<IPart['imageLink']>;
  imageLinkContentType: FormControl<IPart['imageLinkContentType']>;
  videoLink: FormControl<IPart['videoLink']>;
  course: FormControl<IPart['course']>;
  part1: FormControl<IPart['part1']>;
};

export type PartFormGroup = FormGroup<PartFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PartFormService {
  createPartFormGroup(part: PartFormGroupInput = { id: null }): PartFormGroup {
    const partRawValue = {
      ...this.getFormDefaults(),
      ...part,
    };
    return new FormGroup<PartFormGroupContent>({
      id: new FormControl(
        { value: partRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titleAr: new FormControl(partRawValue.titleAr, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      titleLat: new FormControl(partRawValue.titleLat, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(partRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      duration: new FormControl(partRawValue.duration),
      imageLink: new FormControl(partRawValue.imageLink),
      imageLinkContentType: new FormControl(partRawValue.imageLinkContentType),
      videoLink: new FormControl(partRawValue.videoLink),
      course: new FormControl(partRawValue.course),
      part1: new FormControl(partRawValue.part1),
    });
  }

  getPart(form: PartFormGroup): IPart | NewPart {
    return form.getRawValue() as IPart | NewPart;
  }

  resetForm(form: PartFormGroup, part: PartFormGroupInput): void {
    const partRawValue = { ...this.getFormDefaults(), ...part };
    form.reset(
      {
        ...partRawValue,
        id: { value: partRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PartFormDefaults {
    return {
      id: null,
    };
  }
}
