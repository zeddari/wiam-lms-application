import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IProgressionMode } from '../progression-mode.model';
import { ProgressionModeService } from '../service/progression-mode.service';
import { ProgressionModeFormService, ProgressionModeFormGroup } from './progression-mode-form.service';

@Component({
  standalone: true,
  selector: 'jhi-progression-mode-update',
  templateUrl: './progression-mode-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ProgressionModeUpdateComponent implements OnInit {
  isSaving = false;
  progressionMode: IProgressionMode | null = null;

  editForm: ProgressionModeFormGroup = this.progressionModeFormService.createProgressionModeFormGroup();

  constructor(
    protected progressionModeService: ProgressionModeService,
    protected progressionModeFormService: ProgressionModeFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ progressionMode }) => {
      this.progressionMode = progressionMode;
      if (progressionMode) {
        this.updateForm(progressionMode);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const progressionMode = this.progressionModeFormService.getProgressionMode(this.editForm);
    if (progressionMode.id !== null) {
      this.subscribeToSaveResponse(this.progressionModeService.update(progressionMode));
    } else {
      this.subscribeToSaveResponse(this.progressionModeService.create(progressionMode));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgressionMode>>): void {
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

  protected updateForm(progressionMode: IProgressionMode): void {
    this.progressionMode = progressionMode;
    this.progressionModeFormService.resetForm(this.editForm, progressionMode);
  }
}
