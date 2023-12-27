import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISessionProvider } from 'app/entities/session-provider/session-provider.model';
import { SessionProviderService } from 'app/entities/session-provider/service/session-provider.service';
import { ISessionLink } from '../session-link.model';
import { SessionLinkService } from '../service/session-link.service';
import { SessionLinkFormService, SessionLinkFormGroup } from './session-link-form.service';

@Component({
  standalone: true,
  selector: 'jhi-session-link-update',
  templateUrl: './session-link-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SessionLinkUpdateComponent implements OnInit {
  isSaving = false;
  sessionLink: ISessionLink | null = null;

  sessionProvidersSharedCollection: ISessionProvider[] = [];

  editForm: SessionLinkFormGroup = this.sessionLinkFormService.createSessionLinkFormGroup();

  constructor(
    protected sessionLinkService: SessionLinkService,
    protected sessionLinkFormService: SessionLinkFormService,
    protected sessionProviderService: SessionProviderService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareSessionProvider = (o1: ISessionProvider | null, o2: ISessionProvider | null): boolean =>
    this.sessionProviderService.compareSessionProvider(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ sessionLink }) => {
      this.sessionLink = sessionLink;
      if (sessionLink) {
        this.updateForm(sessionLink);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const sessionLink = this.sessionLinkFormService.getSessionLink(this.editForm);
    if (sessionLink.id !== null) {
      this.subscribeToSaveResponse(this.sessionLinkService.update(sessionLink));
    } else {
      this.subscribeToSaveResponse(this.sessionLinkService.create(sessionLink));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISessionLink>>): void {
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

  protected updateForm(sessionLink: ISessionLink): void {
    this.sessionLink = sessionLink;
    this.sessionLinkFormService.resetForm(this.editForm, sessionLink);

    this.sessionProvidersSharedCollection = this.sessionProviderService.addSessionProviderToCollectionIfMissing<ISessionProvider>(
      this.sessionProvidersSharedCollection,
      sessionLink.provider,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.sessionProviderService
      .query()
      .pipe(map((res: HttpResponse<ISessionProvider[]>) => res.body ?? []))
      .pipe(
        map((sessionProviders: ISessionProvider[]) =>
          this.sessionProviderService.addSessionProviderToCollectionIfMissing<ISessionProvider>(
            sessionProviders,
            this.sessionLink?.provider,
          ),
        ),
      )
      .subscribe((sessionProviders: ISessionProvider[]) => (this.sessionProvidersSharedCollection = sessionProviders));
  }
}
