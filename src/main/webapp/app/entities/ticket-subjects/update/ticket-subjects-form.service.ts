import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ITicketSubjects, NewTicketSubjects } from '../ticket-subjects.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITicketSubjects for edit and NewTicketSubjectsFormGroupInput for create.
 */
type TicketSubjectsFormGroupInput = ITicketSubjects | PartialWithRequiredKeyOf<NewTicketSubjects>;

type TicketSubjectsFormDefaults = Pick<NewTicketSubjects, 'id'>;

type TicketSubjectsFormGroupContent = {
  id: FormControl<ITicketSubjects['id'] | NewTicketSubjects['id']>;
  title: FormControl<ITicketSubjects['title']>;
  description: FormControl<ITicketSubjects['description']>;
};

export type TicketSubjectsFormGroup = FormGroup<TicketSubjectsFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TicketSubjectsFormService {
  createTicketSubjectsFormGroup(ticketSubjects: TicketSubjectsFormGroupInput = { id: null }): TicketSubjectsFormGroup {
    const ticketSubjectsRawValue = {
      ...this.getFormDefaults(),
      ...ticketSubjects,
    };
    return new FormGroup<TicketSubjectsFormGroupContent>({
      id: new FormControl(
        { value: ticketSubjectsRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(ticketSubjectsRawValue.title, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(ticketSubjectsRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
    });
  }

  getTicketSubjects(form: TicketSubjectsFormGroup): ITicketSubjects | NewTicketSubjects {
    return form.getRawValue() as ITicketSubjects | NewTicketSubjects;
  }

  resetForm(form: TicketSubjectsFormGroup, ticketSubjects: TicketSubjectsFormGroupInput): void {
    const ticketSubjectsRawValue = { ...this.getFormDefaults(), ...ticketSubjects };
    form.reset(
      {
        ...ticketSubjectsRawValue,
        id: { value: ticketSubjectsRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): TicketSubjectsFormDefaults {
    return {
      id: null,
    };
  }
}
