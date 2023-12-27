import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICoteryHistory, NewCoteryHistory } from '../cotery-history.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICoteryHistory for edit and NewCoteryHistoryFormGroupInput for create.
 */
type CoteryHistoryFormGroupInput = ICoteryHistory | PartialWithRequiredKeyOf<NewCoteryHistory>;

type CoteryHistoryFormDefaults = Pick<NewCoteryHistory, 'id'>;

type CoteryHistoryFormGroupContent = {
  id: FormControl<ICoteryHistory['id'] | NewCoteryHistory['id']>;
  date: FormControl<ICoteryHistory['date']>;
  coteryName: FormControl<ICoteryHistory['coteryName']>;
  studentFullName: FormControl<ICoteryHistory['studentFullName']>;
  attendanceStatus: FormControl<ICoteryHistory['attendanceStatus']>;
  student2: FormControl<ICoteryHistory['student2']>;
  student: FormControl<ICoteryHistory['student']>;
};

export type CoteryHistoryFormGroup = FormGroup<CoteryHistoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CoteryHistoryFormService {
  createCoteryHistoryFormGroup(coteryHistory: CoteryHistoryFormGroupInput = { id: null }): CoteryHistoryFormGroup {
    const coteryHistoryRawValue = {
      ...this.getFormDefaults(),
      ...coteryHistory,
    };
    return new FormGroup<CoteryHistoryFormGroupContent>({
      id: new FormControl(
        { value: coteryHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      date: new FormControl(coteryHistoryRawValue.date),
      coteryName: new FormControl(coteryHistoryRawValue.coteryName),
      studentFullName: new FormControl(coteryHistoryRawValue.studentFullName),
      attendanceStatus: new FormControl(coteryHistoryRawValue.attendanceStatus),
      student2: new FormControl(coteryHistoryRawValue.student2),
      student: new FormControl(coteryHistoryRawValue.student),
    });
  }

  getCoteryHistory(form: CoteryHistoryFormGroup): ICoteryHistory | NewCoteryHistory {
    return form.getRawValue() as ICoteryHistory | NewCoteryHistory;
  }

  resetForm(form: CoteryHistoryFormGroup, coteryHistory: CoteryHistoryFormGroupInput): void {
    const coteryHistoryRawValue = { ...this.getFormDefaults(), ...coteryHistory };
    form.reset(
      {
        ...coteryHistoryRawValue,
        id: { value: coteryHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CoteryHistoryFormDefaults {
    return {
      id: null,
    };
  }
}
