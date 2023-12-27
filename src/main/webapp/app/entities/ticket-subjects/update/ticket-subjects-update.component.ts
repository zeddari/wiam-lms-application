import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITicketSubjects } from '../ticket-subjects.model';
import { TicketSubjectsService } from '../service/ticket-subjects.service';
import { TicketSubjectsFormService, TicketSubjectsFormGroup } from './ticket-subjects-form.service';

@Component({
  standalone: true,
  selector: 'jhi-ticket-subjects-update',
  templateUrl: './ticket-subjects-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TicketSubjectsUpdateComponent implements OnInit {
  isSaving = false;
  ticketSubjects: ITicketSubjects | null = null;

  editForm: TicketSubjectsFormGroup = this.ticketSubjectsFormService.createTicketSubjectsFormGroup();

  constructor(
    protected ticketSubjectsService: TicketSubjectsService,
    protected ticketSubjectsFormService: TicketSubjectsFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ticketSubjects }) => {
      this.ticketSubjects = ticketSubjects;
      if (ticketSubjects) {
        this.updateForm(ticketSubjects);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ticketSubjects = this.ticketSubjectsFormService.getTicketSubjects(this.editForm);
    if (ticketSubjects.id !== null) {
      this.subscribeToSaveResponse(this.ticketSubjectsService.update(ticketSubjects));
    } else {
      this.subscribeToSaveResponse(this.ticketSubjectsService.create(ticketSubjects));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITicketSubjects>>): void {
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

  protected updateForm(ticketSubjects: ITicketSubjects): void {
    this.ticketSubjects = ticketSubjects;
    this.ticketSubjectsFormService.resetForm(this.editForm, ticketSubjects);
  }
}
