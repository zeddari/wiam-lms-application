import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IQuizCertificate, NewQuizCertificate } from '../quiz-certificate.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IQuizCertificate for edit and NewQuizCertificateFormGroupInput for create.
 */
type QuizCertificateFormGroupInput = IQuizCertificate | PartialWithRequiredKeyOf<NewQuizCertificate>;

type QuizCertificateFormDefaults = Pick<NewQuizCertificate, 'id' | 'isActive' | 'students' | 'questions'>;

type QuizCertificateFormGroupContent = {
  id: FormControl<IQuizCertificate['id'] | NewQuizCertificate['id']>;
  title: FormControl<IQuizCertificate['title']>;
  description: FormControl<IQuizCertificate['description']>;
  isActive: FormControl<IQuizCertificate['isActive']>;
  students: FormControl<IQuizCertificate['students']>;
  questions: FormControl<IQuizCertificate['questions']>;
  part: FormControl<IQuizCertificate['part']>;
  session: FormControl<IQuizCertificate['session']>;
  type: FormControl<IQuizCertificate['type']>;
};

export type QuizCertificateFormGroup = FormGroup<QuizCertificateFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class QuizCertificateFormService {
  createQuizCertificateFormGroup(quizCertificate: QuizCertificateFormGroupInput = { id: null }): QuizCertificateFormGroup {
    const quizCertificateRawValue = {
      ...this.getFormDefaults(),
      ...quizCertificate,
    };
    return new FormGroup<QuizCertificateFormGroupContent>({
      id: new FormControl(
        { value: quizCertificateRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(quizCertificateRawValue.title, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(quizCertificateRawValue.description, {
        validators: [Validators.required, Validators.maxLength(500)],
      }),
      isActive: new FormControl(quizCertificateRawValue.isActive, {
        validators: [Validators.required],
      }),
      students: new FormControl(quizCertificateRawValue.students ?? []),
      questions: new FormControl(quizCertificateRawValue.questions ?? []),
      part: new FormControl(quizCertificateRawValue.part),
      session: new FormControl(quizCertificateRawValue.session),
      type: new FormControl(quizCertificateRawValue.type),
    });
  }

  getQuizCertificate(form: QuizCertificateFormGroup): IQuizCertificate | NewQuizCertificate {
    return form.getRawValue() as IQuizCertificate | NewQuizCertificate;
  }

  resetForm(form: QuizCertificateFormGroup, quizCertificate: QuizCertificateFormGroupInput): void {
    const quizCertificateRawValue = { ...this.getFormDefaults(), ...quizCertificate };
    form.reset(
      {
        ...quizCertificateRawValue,
        id: { value: quizCertificateRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): QuizCertificateFormDefaults {
    return {
      id: null,
      isActive: false,
      students: [],
      questions: [],
    };
  }
}
