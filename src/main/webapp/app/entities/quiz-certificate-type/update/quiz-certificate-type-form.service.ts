import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IQuizCertificateType, NewQuizCertificateType } from '../quiz-certificate-type.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IQuizCertificateType for edit and NewQuizCertificateTypeFormGroupInput for create.
 */
type QuizCertificateTypeFormGroupInput = IQuizCertificateType | PartialWithRequiredKeyOf<NewQuizCertificateType>;

type QuizCertificateTypeFormDefaults = Pick<NewQuizCertificateType, 'id'>;

type QuizCertificateTypeFormGroupContent = {
  id: FormControl<IQuizCertificateType['id'] | NewQuizCertificateType['id']>;
  titleAr: FormControl<IQuizCertificateType['titleAr']>;
  titleLat: FormControl<IQuizCertificateType['titleLat']>;
};

export type QuizCertificateTypeFormGroup = FormGroup<QuizCertificateTypeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class QuizCertificateTypeFormService {
  createQuizCertificateTypeFormGroup(quizCertificateType: QuizCertificateTypeFormGroupInput = { id: null }): QuizCertificateTypeFormGroup {
    const quizCertificateTypeRawValue = {
      ...this.getFormDefaults(),
      ...quizCertificateType,
    };
    return new FormGroup<QuizCertificateTypeFormGroupContent>({
      id: new FormControl(
        { value: quizCertificateTypeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titleAr: new FormControl(quizCertificateTypeRawValue.titleAr, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      titleLat: new FormControl(quizCertificateTypeRawValue.titleLat, {
        validators: [Validators.maxLength(20)],
      }),
    });
  }

  getQuizCertificateType(form: QuizCertificateTypeFormGroup): IQuizCertificateType | NewQuizCertificateType {
    return form.getRawValue() as IQuizCertificateType | NewQuizCertificateType;
  }

  resetForm(form: QuizCertificateTypeFormGroup, quizCertificateType: QuizCertificateTypeFormGroupInput): void {
    const quizCertificateTypeRawValue = { ...this.getFormDefaults(), ...quizCertificateType };
    form.reset(
      {
        ...quizCertificateTypeRawValue,
        id: { value: quizCertificateTypeRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): QuizCertificateTypeFormDefaults {
    return {
      id: null,
    };
  }
}
