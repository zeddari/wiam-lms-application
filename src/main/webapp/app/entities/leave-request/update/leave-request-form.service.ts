import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ILeaveRequest, NewLeaveRequest } from '../leave-request.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILeaveRequest for edit and NewLeaveRequestFormGroupInput for create.
 */
type LeaveRequestFormGroupInput = ILeaveRequest | PartialWithRequiredKeyOf<NewLeaveRequest>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ILeaveRequest | NewLeaveRequest> = Omit<T, 'from' | 'toDate'> & {
  from?: string | null;
  toDate?: string | null;
};

type LeaveRequestFormRawValue = FormValueOf<ILeaveRequest>;

type NewLeaveRequestFormRawValue = FormValueOf<NewLeaveRequest>;

type LeaveRequestFormDefaults = Pick<NewLeaveRequest, 'id' | 'from' | 'toDate'>;

type LeaveRequestFormGroupContent = {
  id: FormControl<LeaveRequestFormRawValue['id'] | NewLeaveRequest['id']>;
  title: FormControl<LeaveRequestFormRawValue['title']>;
  from: FormControl<LeaveRequestFormRawValue['from']>;
  toDate: FormControl<LeaveRequestFormRawValue['toDate']>;
  details: FormControl<LeaveRequestFormRawValue['details']>;
};

export type LeaveRequestFormGroup = FormGroup<LeaveRequestFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LeaveRequestFormService {
  createLeaveRequestFormGroup(leaveRequest: LeaveRequestFormGroupInput = { id: null }): LeaveRequestFormGroup {
    const leaveRequestRawValue = this.convertLeaveRequestToLeaveRequestRawValue({
      ...this.getFormDefaults(),
      ...leaveRequest,
    });
    return new FormGroup<LeaveRequestFormGroupContent>({
      id: new FormControl(
        { value: leaveRequestRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(leaveRequestRawValue.title),
      from: new FormControl(leaveRequestRawValue.from, {
        validators: [Validators.required],
      }),
      toDate: new FormControl(leaveRequestRawValue.toDate, {
        validators: [Validators.required],
      }),
      details: new FormControl(leaveRequestRawValue.details),
    });
  }

  getLeaveRequest(form: LeaveRequestFormGroup): ILeaveRequest | NewLeaveRequest {
    return this.convertLeaveRequestRawValueToLeaveRequest(form.getRawValue() as LeaveRequestFormRawValue | NewLeaveRequestFormRawValue);
  }

  resetForm(form: LeaveRequestFormGroup, leaveRequest: LeaveRequestFormGroupInput): void {
    const leaveRequestRawValue = this.convertLeaveRequestToLeaveRequestRawValue({ ...this.getFormDefaults(), ...leaveRequest });
    form.reset(
      {
        ...leaveRequestRawValue,
        id: { value: leaveRequestRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): LeaveRequestFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      from: currentTime,
      toDate: currentTime,
    };
  }

  private convertLeaveRequestRawValueToLeaveRequest(
    rawLeaveRequest: LeaveRequestFormRawValue | NewLeaveRequestFormRawValue,
  ): ILeaveRequest | NewLeaveRequest {
    return {
      ...rawLeaveRequest,
      from: dayjs(rawLeaveRequest.from, DATE_TIME_FORMAT),
      toDate: dayjs(rawLeaveRequest.toDate, DATE_TIME_FORMAT),
    };
  }

  private convertLeaveRequestToLeaveRequestRawValue(
    leaveRequest: ILeaveRequest | (Partial<NewLeaveRequest> & LeaveRequestFormDefaults),
  ): LeaveRequestFormRawValue | PartialWithRequiredKeyOf<NewLeaveRequestFormRawValue> {
    return {
      ...leaveRequest,
      from: leaveRequest.from ? leaveRequest.from.format(DATE_TIME_FORMAT) : undefined,
      toDate: leaveRequest.toDate ? leaveRequest.toDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
