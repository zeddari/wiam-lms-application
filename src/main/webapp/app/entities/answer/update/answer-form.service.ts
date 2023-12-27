import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAnswer, NewAnswer } from '../answer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAnswer for edit and NewAnswerFormGroupInput for create.
 */
type AnswerFormGroupInput = IAnswer | PartialWithRequiredKeyOf<NewAnswer>;

type AnswerFormDefaults = Pick<NewAnswer, 'id' | 'a1v' | 'a2v' | 'a3v' | 'a4v' | 'result'>;

type AnswerFormGroupContent = {
  id: FormControl<IAnswer['id'] | NewAnswer['id']>;
  a1v: FormControl<IAnswer['a1v']>;
  a2v: FormControl<IAnswer['a2v']>;
  a3v: FormControl<IAnswer['a3v']>;
  a4v: FormControl<IAnswer['a4v']>;
  result: FormControl<IAnswer['result']>;
  question: FormControl<IAnswer['question']>;
  student: FormControl<IAnswer['student']>;
};

export type AnswerFormGroup = FormGroup<AnswerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AnswerFormService {
  createAnswerFormGroup(answer: AnswerFormGroupInput = { id: null }): AnswerFormGroup {
    const answerRawValue = {
      ...this.getFormDefaults(),
      ...answer,
    };
    return new FormGroup<AnswerFormGroupContent>({
      id: new FormControl(
        { value: answerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      a1v: new FormControl(answerRawValue.a1v, {
        validators: [Validators.required],
      }),
      a2v: new FormControl(answerRawValue.a2v, {
        validators: [Validators.required],
      }),
      a3v: new FormControl(answerRawValue.a3v),
      a4v: new FormControl(answerRawValue.a4v),
      result: new FormControl(answerRawValue.result, {
        validators: [Validators.required],
      }),
      question: new FormControl(answerRawValue.question),
      student: new FormControl(answerRawValue.student),
    });
  }

  getAnswer(form: AnswerFormGroup): IAnswer | NewAnswer {
    return form.getRawValue() as IAnswer | NewAnswer;
  }

  resetForm(form: AnswerFormGroup, answer: AnswerFormGroupInput): void {
    const answerRawValue = { ...this.getFormDefaults(), ...answer };
    form.reset(
      {
        ...answerRawValue,
        id: { value: answerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AnswerFormDefaults {
    return {
      id: null,
      a1v: false,
      a2v: false,
      a3v: false,
      a4v: false,
      result: false,
    };
  }
}
