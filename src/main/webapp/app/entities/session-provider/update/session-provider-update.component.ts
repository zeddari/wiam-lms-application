import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISessionProvider } from '../session-provider.model';
import { SessionProviderService } from '../service/session-provider.service';
import { SessionProviderFormService, SessionProviderFormGroup } from './session-provider-form.service';

@Component({
  standalone: true,
  selector: 'jhi-session-provider-update',
  templateUrl: './session-provider-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SessionProviderUpdateComponent implements OnInit {
  isSaving = false;
  sessionProvider: ISessionProvider | null = null;

  editForm: SessionProviderFormGroup = this.sessionProviderFormService.createSessionProviderFormGroup();

  constructor(
    protected sessionProviderService: SessionProviderService,
    protected sessionProviderFormService: SessionProviderFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sessionProvider }) => {
      this.sessionProvider = sessionProvider;
      if (sessionProvider) {
        this.updateForm(sessionProvider);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sessionProvider = this.sessionProviderFormService.getSessionProvider(this.editForm);
    if (sessionProvider.id !== null) {
      this.subscribeToSaveResponse(this.sessionProviderService.update(sessionProvider));
    } else {
      this.subscribeToSaveResponse(this.sessionProviderService.create(sessionProvider));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISessionProvider>>): void {
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

  protected updateForm(sessionProvider: ISessionProvider): void {
    this.sessionProvider = sessionProvider;
    this.sessionProviderFormService.resetForm(this.editForm, sessionProvider);
  }
}
