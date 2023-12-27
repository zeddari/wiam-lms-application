import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ICourse, NewCourse } from '../course.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICourse for edit and NewCourseFormGroupInput for create.
 */
type CourseFormGroupInput = ICourse | PartialWithRequiredKeyOf<NewCourse>;

type CourseFormDefaults = Pick<NewCourse, 'id' | 'type' | 'isActive' | 'isConfirmed'>;

type CourseFormGroupContent = {
  id: FormControl<ICourse['id'] | NewCourse['id']>;
  titleAr: FormControl<ICourse['titleAr']>;
  titleLat: FormControl<ICourse['titleLat']>;
  description: FormControl<ICourse['description']>;
  subTitles: FormControl<ICourse['subTitles']>;
  requirement: FormControl<ICourse['requirement']>;
  duration: FormControl<ICourse['duration']>;
  option: FormControl<ICourse['option']>;
  type: FormControl<ICourse['type']>;
  imageLink: FormControl<ICourse['imageLink']>;
  imageLinkContentType: FormControl<ICourse['imageLinkContentType']>;
  videoLink: FormControl<ICourse['videoLink']>;
  price: FormControl<ICourse['price']>;
  isActive: FormControl<ICourse['isActive']>;
  activateAt: FormControl<ICourse['activateAt']>;
  isConfirmed: FormControl<ICourse['isConfirmed']>;
  confirmedAt: FormControl<ICourse['confirmedAt']>;
  topic1: FormControl<ICourse['topic1']>;
  level: FormControl<ICourse['level']>;
  professor1: FormControl<ICourse['professor1']>;
};

export type CourseFormGroup = FormGroup<CourseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CourseFormService {
  createCourseFormGroup(course: CourseFormGroupInput = { id: null }): CourseFormGroup {
    const courseRawValue = {
      ...this.getFormDefaults(),
      ...course,
    };
    return new FormGroup<CourseFormGroupContent>({
      id: new FormControl(
        { value: courseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titleAr: new FormControl(courseRawValue.titleAr, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      titleLat: new FormControl(courseRawValue.titleLat, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(courseRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      subTitles: new FormControl(courseRawValue.subTitles, {
        validators: [Validators.maxLength(500)],
      }),
      requirement: new FormControl(courseRawValue.requirement, {
        validators: [Validators.maxLength(500)],
      }),
      duration: new FormControl(courseRawValue.duration),
      option: new FormControl(courseRawValue.option, {
        validators: [Validators.maxLength(500)],
      }),
      type: new FormControl(courseRawValue.type),
      imageLink: new FormControl(courseRawValue.imageLink),
      imageLinkContentType: new FormControl(courseRawValue.imageLinkContentType),
      videoLink: new FormControl(courseRawValue.videoLink),
      price: new FormControl(courseRawValue.price, {
        validators: [Validators.min(0)],
      }),
      isActive: new FormControl(courseRawValue.isActive),
      activateAt: new FormControl(courseRawValue.activateAt),
      isConfirmed: new FormControl(courseRawValue.isConfirmed),
      confirmedAt: new FormControl(courseRawValue.confirmedAt),
      topic1: new FormControl(courseRawValue.topic1),
      level: new FormControl(courseRawValue.level),
      professor1: new FormControl(courseRawValue.professor1),
    });
  }

  getCourse(form: CourseFormGroup): ICourse | NewCourse {
    return form.getRawValue() as ICourse | NewCourse;
  }

  resetForm(form: CourseFormGroup, course: CourseFormGroupInput): void {
    const courseRawValue = { ...this.getFormDefaults(), ...course };
    form.reset(
      {
        ...courseRawValue,
        id: { value: courseRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): CourseFormDefaults {
    return {
      id: null,
      type: false,
      isActive: false,
      isConfirmed: false,
    };
  }
}
