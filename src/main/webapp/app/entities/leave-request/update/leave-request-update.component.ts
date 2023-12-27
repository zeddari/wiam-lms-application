import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { LeaveRequestService } from '../service/leave-request.service';
import { ILeaveRequest } from '../leave-request.model';
import { LeaveRequestFormService, LeaveRequestFormGroup } from './leave-request-form.service';

@Component({
  standalone: true,
  selector: 'jhi-leave-request-update',
  templateUrl: './leave-request-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LeaveRequestUpdateComponent implements OnInit {
  isSaving = false;
  leaveRequest: ILeaveRequest | null = null;

  editForm: LeaveRequestFormGroup = this.leaveRequestFormService.createLeaveRequestFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected leaveRequestService: LeaveRequestService,
    protected leaveRequestFormService: LeaveRequestFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ leaveRequest }) => {
      this.leaveRequest = leaveRequest;
      if (leaveRequest) {
        this.updateForm(leaveRequest);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(
          new EventWithContent<AlertError>('wiamLmsApplicationApp.error', { ...err, key: 'error.file.' + err.key }),
        ),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const leaveRequest = this.leaveRequestFormService.getLeaveRequest(this.editForm);
    if (leaveRequest.id !== null) {
      this.subscribeToSaveResponse(this.leaveRequestService.update(leaveRequest));
    } else {
      this.subscribeToSaveResponse(this.leaveRequestService.create(leaveRequest));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILeaveRequest>>): void {
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

  protected updateForm(leaveRequest: ILeaveRequest): void {
    this.leaveRequest = leaveRequest;
    this.leaveRequestFormService.resetForm(this.editForm, leaveRequest);
  }
}
