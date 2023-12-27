import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPayment, NewPayment } from '../payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPayment for edit and NewPaymentFormGroupInput for create.
 */
type PaymentFormGroupInput = IPayment | PartialWithRequiredKeyOf<NewPayment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPayment | NewPayment> = Omit<T, 'paidAt'> & {
  paidAt?: string | null;
};

type PaymentFormRawValue = FormValueOf<IPayment>;

type NewPaymentFormRawValue = FormValueOf<NewPayment>;

type PaymentFormDefaults = Pick<NewPayment, 'id' | 'paidAt'>;

type PaymentFormGroupContent = {
  id: FormControl<PaymentFormRawValue['id'] | NewPayment['id']>;
  paymentMethod: FormControl<PaymentFormRawValue['paymentMethod']>;
  paiedBy: FormControl<PaymentFormRawValue['paiedBy']>;
  mode: FormControl<PaymentFormRawValue['mode']>;
  poof: FormControl<PaymentFormRawValue['poof']>;
  poofContentType: FormControl<PaymentFormRawValue['poofContentType']>;
  paidAt: FormControl<PaymentFormRawValue['paidAt']>;
  amount: FormControl<PaymentFormRawValue['amount']>;
  type: FormControl<PaymentFormRawValue['type']>;
  fromMonth: FormControl<PaymentFormRawValue['fromMonth']>;
  toMonth: FormControl<PaymentFormRawValue['toMonth']>;
  details: FormControl<PaymentFormRawValue['details']>;
  currency: FormControl<PaymentFormRawValue['currency']>;
  enrolment: FormControl<PaymentFormRawValue['enrolment']>;
};

export type PaymentFormGroup = FormGroup<PaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaymentFormService {
  createPaymentFormGroup(payment: PaymentFormGroupInput = { id: null }): PaymentFormGroup {
    const paymentRawValue = this.convertPaymentToPaymentRawValue({
      ...this.getFormDefaults(),
      ...payment,
    });
    return new FormGroup<PaymentFormGroupContent>({
      id: new FormControl(
        { value: paymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      paymentMethod: new FormControl(paymentRawValue.paymentMethod, {
        validators: [Validators.required],
      }),
      paiedBy: new FormControl(paymentRawValue.paiedBy, {
        validators: [Validators.required],
      }),
      mode: new FormControl(paymentRawValue.mode, {
        validators: [Validators.required],
      }),
      poof: new FormControl(paymentRawValue.poof),
      poofContentType: new FormControl(paymentRawValue.poofContentType),
      paidAt: new FormControl(paymentRawValue.paidAt, {
        validators: [Validators.required],
      }),
      amount: new FormControl(paymentRawValue.amount),
      type: new FormControl(paymentRawValue.type, {
        validators: [Validators.required],
      }),
      fromMonth: new FormControl(paymentRawValue.fromMonth, {
        validators: [Validators.required],
      }),
      toMonth: new FormControl(paymentRawValue.toMonth, {
        validators: [Validators.required],
      }),
      details: new FormControl(paymentRawValue.details),
      currency: new FormControl(paymentRawValue.currency),
      enrolment: new FormControl(paymentRawValue.enrolment),
    });
  }

  getPayment(form: PaymentFormGroup): IPayment | NewPayment {
    return this.convertPaymentRawValueToPayment(form.getRawValue() as PaymentFormRawValue | NewPaymentFormRawValue);
  }

  resetForm(form: PaymentFormGroup, payment: PaymentFormGroupInput): void {
    const paymentRawValue = this.convertPaymentToPaymentRawValue({ ...this.getFormDefaults(), ...payment });
    form.reset(
      {
        ...paymentRawValue,
        id: { value: paymentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PaymentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      paidAt: currentTime,
    };
  }

  private convertPaymentRawValueToPayment(rawPayment: PaymentFormRawValue | NewPaymentFormRawValue): IPayment | NewPayment {
    return {
      ...rawPayment,
      paidAt: dayjs(rawPayment.paidAt, DATE_TIME_FORMAT),
    };
  }

  private convertPaymentToPaymentRawValue(
    payment: IPayment | (Partial<NewPayment> & PaymentFormDefaults),
  ): PaymentFormRawValue | PartialWithRequiredKeyOf<NewPaymentFormRawValue> {
    return {
      ...payment,
      paidAt: payment.paidAt ? payment.paidAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
