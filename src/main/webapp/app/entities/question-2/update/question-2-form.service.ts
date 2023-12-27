import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IQuestion2, NewQuestion2 } from '../question-2.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IQuestion2 for edit and NewQuestion2FormGroupInput for create.
 */
type Question2FormGroupInput = IQuestion2 | PartialWithRequiredKeyOf<NewQuestion2>;

type Question2FormDefaults = Pick<NewQuestion2, 'id'>;

type Question2FormGroupContent = {
  id: FormControl<IQuestion2['id'] | NewQuestion2['id']>;
  questionTitle: FormControl<IQuestion2['questionTitle']>;
  questionType: FormControl<IQuestion2['questionType']>;
  questionDescription: FormControl<IQuestion2['questionDescription']>;
  questionPoint: FormControl<IQuestion2['questionPoint']>;
  questionSubject: FormControl<IQuestion2['questionSubject']>;
  questionStatus: FormControl<IQuestion2['questionStatus']>;
};

export type Question2FormGroup = FormGroup<Question2FormGroupContent>;

@Injectable({ providedIn: 'root' })
export class Question2FormService {
  createQuestion2FormGroup(question2: Question2FormGroupInput = { id: null }): Question2FormGroup {
    const question2RawValue = {
      ...this.getFormDefaults(),
      ...question2,
    };
    return new FormGroup<Question2FormGroupContent>({
      id: new FormControl(
        { value: question2RawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      questionTitle: new FormControl(question2RawValue.questionTitle),
      questionType: new FormControl(question2RawValue.questionType),
      questionDescription: new FormControl(question2RawValue.questionDescription),
      questionPoint: new FormControl(question2RawValue.questionPoint),
      questionSubject: new FormControl(question2RawValue.questionSubject),
      questionStatus: new FormControl(question2RawValue.questionStatus),
    });
  }

  getQuestion2(form: Question2FormGroup): IQuestion2 | NewQuestion2 {
    return form.getRawValue() as IQuestion2 | NewQuestion2;
  }

  resetForm(form: Question2FormGroup, question2: Question2FormGroupInput): void {
    const question2RawValue = { ...this.getFormDefaults(), ...question2 };
    form.reset(
      {
        ...question2RawValue,
        id: { value: question2RawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): Question2FormDefaults {
    return {
      id: null,
    };
  }
}
