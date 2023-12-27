import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IJobTitle } from '../job-title.model';
import { JobTitleService } from '../service/job-title.service';
import { JobTitleFormService, JobTitleFormGroup } from './job-title-form.service';

@Component({
  standalone: true,
  selector: 'jhi-job-title-update',
  templateUrl: './job-title-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class JobTitleUpdateComponent implements OnInit {
  isSaving = false;
  jobTitle: IJobTitle | null = null;

  editForm: JobTitleFormGroup = this.jobTitleFormService.createJobTitleFormGroup();

  constructor(
    protected jobTitleService: JobTitleService,
    protected jobTitleFormService: JobTitleFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jobTitle }) => {
      this.jobTitle = jobTitle;
      if (jobTitle) {
        this.updateForm(jobTitle);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const jobTitle = this.jobTitleFormService.getJobTitle(this.editForm);
    if (jobTitle.id !== null) {
      this.subscribeToSaveResponse(this.jobTitleService.update(jobTitle));
    } else {
      this.subscribeToSaveResponse(this.jobTitleService.create(jobTitle));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJobTitle>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(jobTitle: IJobTitle): void {
    this.jobTitle = jobTitle;
    this.jobTitleFormService.resetForm(this.editForm, jobTitle);
  }
}
