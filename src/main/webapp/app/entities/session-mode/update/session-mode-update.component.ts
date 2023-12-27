import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISessionMode } from '../session-mode.model';
import { SessionModeService } from '../service/session-mode.service';
import { SessionModeFormService, SessionModeFormGroup } from './session-mode-form.service';

@Component({
  standalone: true,
  selector: 'jhi-session-mode-update',
  templateUrl: './session-mode-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SessionModeUpdateComponent implements OnInit {
  isSaving = false;
  sessionMode: ISessionMode | null = null;

  editForm: SessionModeFormGroup = this.sessionModeFormService.createSessionModeFormGroup();

  constructor(
    protected sessionModeService: SessionModeService,
    protected sessionModeFormService: SessionModeFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sessionMode }) => {
      this.sessionMode = sessionMode;
      if (sessionMode) {
        this.updateForm(sessionMode);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sessionMode = this.sessionModeFormService.getSessionMode(this.editForm);
    if (sessionMode.id !== null) {
      this.subscribeToSaveResponse(this.sessionModeService.update(sessionMode));
    } else {
      this.subscribeToSaveResponse(this.sessionModeService.create(sessionMode));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISessionMode>>): void {
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

  protected updateForm(sessionMode: ISessionMode): void {
    this.sessionMode = sessionMode;
    this.sessionModeFormService.resetForm(this.editForm, sessionMode);
  }
}
