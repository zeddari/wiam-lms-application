import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICoteryHistory } from 'app/entities/cotery-history/cotery-history.model';
import { CoteryHistoryService } from 'app/entities/cotery-history/service/cotery-history.service';
import { Sourate } from 'app/entities/enumerations/sourate.model';
import { Tilawa } from 'app/entities/enumerations/tilawa.model';
import { FollowUpService } from '../service/follow-up.service';
import { IFollowUp } from '../follow-up.model';
import { FollowUpFormService, FollowUpFormGroup } from './follow-up-form.service';

@Component({
  standalone: true,
  selector: 'jhi-follow-up-update',
  templateUrl: './follow-up-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class FollowUpUpdateComponent implements OnInit {
  isSaving = false;
  followUp: IFollowUp | null = null;
  sourateValues = Object.keys(Sourate);
  tilawaValues = Object.keys(Tilawa);

  coteryHistoriesCollection: ICoteryHistory[] = [];

  editForm: FollowUpFormGroup = this.followUpFormService.createFollowUpFormGroup();

  constructor(
    protected followUpService: FollowUpService,
    protected followUpFormService: FollowUpFormService,
    protected coteryHistoryService: CoteryHistoryService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareCoteryHistory = (o1: ICoteryHistory | null, o2: ICoteryHistory | null): boolean =>
    this.coteryHistoryService.compareCoteryHistory(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ followUp }) => {
      this.followUp = followUp;
      if (followUp) {
        this.updateForm(followUp);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const followUp = this.followUpFormService.getFollowUp(this.editForm);
    if (followUp.id !== null) {
      this.subscribeToSaveResponse(this.followUpService.update(followUp));
    } else {
      this.subscribeToSaveResponse(this.followUpService.create(followUp));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFollowUp>>): void {
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

  protected updateForm(followUp: IFollowUp): void {
    this.followUp = followUp;
    this.followUpFormService.resetForm(this.editForm, followUp);

    this.coteryHistoriesCollection = this.coteryHistoryService.addCoteryHistoryToCollectionIfMissing<ICoteryHistory>(
      this.coteryHistoriesCollection,
      followUp.coteryHistory,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.coteryHistoryService
      .query({ filter: 'followup-is-null' })
      .pipe(map((res: HttpResponse<ICoteryHistory[]>) => res.body ?? []))
      .pipe(
        map((coteryHistories: ICoteryHistory[]) =>
          this.coteryHistoryService.addCoteryHistoryToCollectionIfMissing<ICoteryHistory>(coteryHistories, this.followUp?.coteryHistory),
        ),
      )
      .subscribe((coteryHistories: ICoteryHistory[]) => (this.coteryHistoriesCollection = coteryHistories));
  }
}
