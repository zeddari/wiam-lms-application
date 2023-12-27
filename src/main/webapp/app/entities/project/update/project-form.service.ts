import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IProject, NewProject } from '../project.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProject for edit and NewProjectFormGroupInput for create.
 */
type ProjectFormGroupInput = IProject | PartialWithRequiredKeyOf<NewProject>;

type ProjectFormDefaults = Pick<NewProject, 'id' | 'isActive'>;

type ProjectFormGroupContent = {
  id: FormControl<IProject['id'] | NewProject['id']>;
  titleAr: FormControl<IProject['titleAr']>;
  titleLat: FormControl<IProject['titleLat']>;
  description: FormControl<IProject['description']>;
  goals: FormControl<IProject['goals']>;
  requirement: FormControl<IProject['requirement']>;
  imageLink: FormControl<IProject['imageLink']>;
  imageLinkContentType: FormControl<IProject['imageLinkContentType']>;
  videoLink: FormControl<IProject['videoLink']>;
  budget: FormControl<IProject['budget']>;
  isActive: FormControl<IProject['isActive']>;
  activateAt: FormControl<IProject['activateAt']>;
  startDate: FormControl<IProject['startDate']>;
  endDate: FormControl<IProject['endDate']>;
};

export type ProjectFormGroup = FormGroup<ProjectFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProjectFormService {
  createProjectFormGroup(project: ProjectFormGroupInput = { id: null }): ProjectFormGroup {
    const projectRawValue = {
      ...this.getFormDefaults(),
      ...project,
    };
    return new FormGroup<ProjectFormGroupContent>({
      id: new FormControl(
        { value: projectRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      titleAr: new FormControl(projectRawValue.titleAr, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      titleLat: new FormControl(projectRawValue.titleLat, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(projectRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      goals: new FormControl(projectRawValue.goals, {
        validators: [Validators.maxLength(500)],
      }),
      requirement: new FormControl(projectRawValue.requirement, {
        validators: [Validators.maxLength(500)],
      }),
      imageLink: new FormControl(projectRawValue.imageLink),
      imageLinkContentType: new FormControl(projectRawValue.imageLinkContentType),
      videoLink: new FormControl(projectRawValue.videoLink),
      budget: new FormControl(projectRawValue.budget, {
        validators: [Validators.required, Validators.min(0)],
      }),
      isActive: new FormControl(projectRawValue.isActive),
      activateAt: new FormControl(projectRawValue.activateAt),
      startDate: new FormControl(projectRawValue.startDate),
      endDate: new FormControl(projectRawValue.endDate),
    });
  }

  getProject(form: ProjectFormGroup): IProject | NewProject {
    return form.getRawValue() as IProject | NewProject;
  }

  resetForm(form: ProjectFormGroup, project: ProjectFormGroupInput): void {
    const projectRawValue = { ...this.getFormDefaults(), ...project };
    form.reset(
      {
        ...projectRawValue,
        id: { value: projectRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ProjectFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
