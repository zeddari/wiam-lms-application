import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISession, NewSession } from '../session.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISession for edit and NewSessionFormGroupInput for create.
 */
type SessionFormGroupInput = ISession | PartialWithRequiredKeyOf<NewSession>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISession | NewSession> = Omit<T, 'sessionStartTime' | 'sessionEndTime' | 'onceDate'> & {
  sessionStartTime?: string | null;
  sessionEndTime?: string | null;
  onceDate?: string | null;
};

type SessionFormRawValue = FormValueOf<ISession>;

type NewSessionFormRawValue = FormValueOf<NewSession>;

type SessionFormDefaults = Pick<
  NewSession,
  | 'id'
  | 'sessionStartTime'
  | 'sessionEndTime'
  | 'isActive'
  | 'targetedGender'
  | 'onceDate'
  | 'monday'
  | 'tuesday'
  | 'wednesday'
  | 'thursday'
  | 'friday'
  | 'saturday'
  | 'sanday'
  | 'noPeriodeEndDate'
  | 'professors'
  | 'employees'
  | 'links'
>;

type SessionFormGroupContent = {
  id: FormControl<SessionFormRawValue['id'] | NewSession['id']>;
  title: FormControl<SessionFormRawValue['title']>;
  description: FormControl<SessionFormRawValue['description']>;
  sessionStartTime: FormControl<SessionFormRawValue['sessionStartTime']>;
  sessionEndTime: FormControl<SessionFormRawValue['sessionEndTime']>;
  isActive: FormControl<SessionFormRawValue['isActive']>;
  sessionSize: FormControl<SessionFormRawValue['sessionSize']>;
  price: FormControl<SessionFormRawValue['price']>;
  currency: FormControl<SessionFormRawValue['currency']>;
  targetedAge: FormControl<SessionFormRawValue['targetedAge']>;
  targetedGender: FormControl<SessionFormRawValue['targetedGender']>;
  thumbnail: FormControl<SessionFormRawValue['thumbnail']>;
  thumbnailContentType: FormControl<SessionFormRawValue['thumbnailContentType']>;
  planningType: FormControl<SessionFormRawValue['planningType']>;
  onceDate: FormControl<SessionFormRawValue['onceDate']>;
  monday: FormControl<SessionFormRawValue['monday']>;
  tuesday: FormControl<SessionFormRawValue['tuesday']>;
  wednesday: FormControl<SessionFormRawValue['wednesday']>;
  thursday: FormControl<SessionFormRawValue['thursday']>;
  friday: FormControl<SessionFormRawValue['friday']>;
  saturday: FormControl<SessionFormRawValue['saturday']>;
  sanday: FormControl<SessionFormRawValue['sanday']>;
  periodStartDate: FormControl<SessionFormRawValue['periodStartDate']>;
  periodeEndDate: FormControl<SessionFormRawValue['periodeEndDate']>;
  noPeriodeEndDate: FormControl<SessionFormRawValue['noPeriodeEndDate']>;
  professors: FormControl<SessionFormRawValue['professors']>;
  employees: FormControl<SessionFormRawValue['employees']>;
  links: FormControl<SessionFormRawValue['links']>;
  classroom: FormControl<SessionFormRawValue['classroom']>;
  type: FormControl<SessionFormRawValue['type']>;
  mode: FormControl<SessionFormRawValue['mode']>;
  part: FormControl<SessionFormRawValue['part']>;
  jmode: FormControl<SessionFormRawValue['jmode']>;
  group: FormControl<SessionFormRawValue['group']>;
};

export type SessionFormGroup = FormGroup<SessionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SessionFormService {
  createSessionFormGroup(session: SessionFormGroupInput = { id: null }): SessionFormGroup {
    const sessionRawValue = this.convertSessionToSessionRawValue({
      ...this.getFormDefaults(),
      ...session,
    });
    return new FormGroup<SessionFormGroupContent>({
      id: new FormControl(
        { value: sessionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      title: new FormControl(sessionRawValue.title, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(sessionRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      sessionStartTime: new FormControl(sessionRawValue.sessionStartTime, {
        validators: [Validators.required],
      }),
      sessionEndTime: new FormControl(sessionRawValue.sessionEndTime, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(sessionRawValue.isActive, {
        validators: [Validators.required],
      }),
      sessionSize: new FormControl(sessionRawValue.sessionSize, {
        validators: [Validators.required, Validators.min(0), Validators.max(100)],
      }),
      price: new FormControl(sessionRawValue.price, {
        validators: [Validators.min(0)],
      }),
      currency: new FormControl(sessionRawValue.currency, {
        validators: [Validators.required],
      }),
      targetedAge: new FormControl(sessionRawValue.targetedAge, {
        validators: [Validators.required],
      }),
      targetedGender: new FormControl(sessionRawValue.targetedGender, {
        validators: [Validators.required],
      }),
      thumbnail: new FormControl(sessionRawValue.thumbnail),
      thumbnailContentType: new FormControl(sessionRawValue.thumbnailContentType),
      planningType: new FormControl(sessionRawValue.planningType, {
        validators: [Validators.required],
      }),
      onceDate: new FormControl(sessionRawValue.onceDate),
      monday: new FormControl(sessionRawValue.monday),
      tuesday: new FormControl(sessionRawValue.tuesday),
      wednesday: new FormControl(sessionRawValue.wednesday),
      thursday: new FormControl(sessionRawValue.thursday),
      friday: new FormControl(sessionRawValue.friday),
      saturday: new FormControl(sessionRawValue.saturday),
      sanday: new FormControl(sessionRawValue.sanday),
      periodStartDate: new FormControl(sessionRawValue.periodStartDate),
      periodeEndDate: new FormControl(sessionRawValue.periodeEndDate),
      noPeriodeEndDate: new FormControl(sessionRawValue.noPeriodeEndDate),
      professors: new FormControl(sessionRawValue.professors ?? []),
      employees: new FormControl(sessionRawValue.employees ?? []),
      links: new FormControl(sessionRawValue.links ?? []),
      classroom: new FormControl(sessionRawValue.classroom),
      type: new FormControl(sessionRawValue.type),
      mode: new FormControl(sessionRawValue.mode),
      part: new FormControl(sessionRawValue.part),
      jmode: new FormControl(sessionRawValue.jmode),
      group: new FormControl(sessionRawValue.group),
    });
  }

  getSession(form: SessionFormGroup): ISession | NewSession {
    return this.convertSessionRawValueToSession(form.getRawValue() as SessionFormRawValue | NewSessionFormRawValue);
  }

  resetForm(form: SessionFormGroup, session: SessionFormGroupInput): void {
    const sessionRawValue = this.convertSessionToSessionRawValue({ ...this.getFormDefaults(), ...session });
    form.reset(
      {
        ...sessionRawValue,
        id: { value: sessionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SessionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      sessionStartTime: currentTime,
      sessionEndTime: currentTime,
      isActive: false,
      targetedGender: false,
      onceDate: currentTime,
      monday: false,
      tuesday: false,
      wednesday: false,
      thursday: false,
      friday: false,
      saturday: false,
      sanday: false,
      noPeriodeEndDate: false,
      professors: [],
      employees: [],
      links: [],
    };
  }

  private convertSessionRawValueToSession(rawSession: SessionFormRawValue | NewSessionFormRawValue): ISession | NewSession {
    return {
      ...rawSession,
      sessionStartTime: dayjs(rawSession.sessionStartTime, DATE_TIME_FORMAT),
      sessionEndTime: dayjs(rawSession.sessionEndTime, DATE_TIME_FORMAT),
      onceDate: dayjs(rawSession.onceDate, DATE_TIME_FORMAT),
    };
  }

  private convertSessionToSessionRawValue(
    session: ISession | (Partial<NewSession> & SessionFormDefaults),
  ): SessionFormRawValue | PartialWithRequiredKeyOf<NewSessionFormRawValue> {
    return {
      ...session,
      sessionStartTime: session.sessionStartTime ? session.sessionStartTime.format(DATE_TIME_FORMAT) : undefined,
      sessionEndTime: session.sessionEndTime ? session.sessionEndTime.format(DATE_TIME_FORMAT) : undefined,
      onceDate: session.onceDate ? session.onceDate.format(DATE_TIME_FORMAT) : undefined,
      professors: session.professors ?? [],
      employees: session.employees ?? [],
      links: session.links ?? [],
    };
  }
}
