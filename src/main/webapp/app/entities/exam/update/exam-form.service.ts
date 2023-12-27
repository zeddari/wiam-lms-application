import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IExam, NewExam } from '../exam.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExam for edit and NewExamFormGroupInput for create.
 */
type ExamFormGroupInput = IExam | PartialWithRequiredKeyOf<NewExam>;

type ExamFormDefaults = Pick<NewExam, 'id'>;

type ExamFormGroupContent = {
  id: FormControl<IExam['id'] | NewExam['id']>;
  comite: FormControl<IExam['comite']>;
  studentName: FormControl<IExam['studentName']>;
  examName: FormControl<IExam['examName']>;
  examCategory: FormControl<IExam['examCategory']>;
  examType: FormControl<IExam['examType']>;
  tajweedScore: FormControl<IExam['tajweedScore']>;
  hifdScore: FormControl<IExam['hifdScore']>;
  adaeScore: FormControl<IExam['adaeScore']>;
  observation: FormControl<IExam['observation']>;
  decision: FormControl<IExam['decision']>;
};

export type ExamFormGroup = FormGroup<ExamFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExamFormService {
  createExamFormGroup(exam: ExamFormGroupInput = { id: null }): ExamFormGroup {
    const examRawValue = {
      ...this.getFormDefaults(),
      ...exam,
    };
    return new FormGroup<ExamFormGroupContent>({
      id: new FormControl(
        { value: examRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      comite: new FormControl(examRawValue.comite),
      studentName: new FormControl(examRawValue.studentName),
      examName: new FormControl(examRawValue.examName),
      examCategory: new FormControl(examRawValue.examCategory),
      examType: new FormControl(examRawValue.examType),
      tajweedScore: new FormControl(examRawValue.tajweedScore, {
        validators: [Validators.required],
      }),
      hifdScore: new FormControl(examRawValue.hifdScore, {
        validators: [Validators.required],
      }),
      adaeScore: new FormControl(examRawValue.adaeScore, {
        validators: [Validators.required],
      }),
      observation: new FormControl(examRawValue.observation),
      decision: new FormControl(examRawValue.decision, {
        validators: [Validators.required],
      }),
    });
  }

  getExam(form: ExamFormGroup): IExam | NewExam {
    return form.getRawValue() as IExam | NewExam;
  }

  resetForm(form: ExamFormGroup, exam: ExamFormGroupInput): void {
    const examRawValue = { ...this.getFormDefaults(), ...exam };
    form.reset(
      {
        ...examRawValue,
        id: { value: examRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ExamFormDefaults {
    return {
      id: null,
    };
  }
}
