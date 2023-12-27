import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISessionJoinMode } from '../session-join-mode.model';
import { SessionJoinModeService } from '../service/session-join-mode.service';
import { SessionJoinModeFormService, SessionJoinModeFormGroup } from './session-join-mode-form.service';

@Component({
  standalone: true,
  selector: 'jhi-session-join-mode-update',
  templateUrl: './session-join-mode-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SessionJoinModeUpdateComponent implements OnInit {
  isSaving = false;
  sessionJoinMode: ISessionJoinMode | null = null;

  editForm: SessionJoinModeFormGroup = this.sessionJoinModeFormService.createSessionJoinModeFormGroup();

  constructor(
    protected sessionJoinModeService: SessionJoinModeService,
    protected sessionJoinModeFormService: SessionJoinModeFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sessionJoinMode }) => {
      this.sessionJoinMode = sessionJoinMode;
      if (sessionJoinMode) {
        this.updateForm(sessionJoinMode);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sessionJoinMode = this.sessionJoinModeFormService.getSessionJoinMode(this.editForm);
    if (sessionJoinMode.id !== null) {
      this.subscribeToSaveResponse(this.sessionJoinModeService.update(sessionJoinMode));
    } else {
      this.subscribeToSaveResponse(this.sessionJoinModeService.create(sessionJoinMode));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISessionJoinMode>>): void {
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

  protected updateForm(sessionJoinMode: ISessionJoinMode): void {
    this.sessionJoinMode = sessionJoinMode;
    this.sessionJoinModeFormService.resetForm(this.editForm, sessionJoinMode);
  }
}
