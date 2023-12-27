import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { Attendance } from 'app/entities/enumerations/attendance.model';
import { ICotery } from '../cotery.model';
import { CoteryService } from '../service/cotery.service';
import { CoteryFormService, CoteryFormGroup } from './cotery-form.service';

@Component({
  standalone: true,
  selector: 'jhi-cotery-update',
  templateUrl: './cotery-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CoteryUpdateComponent implements OnInit {
  isSaving = false;
  cotery: ICotery | null = null;
  attendanceValues = Object.keys(Attendance);

  editForm: CoteryFormGroup = this.coteryFormService.createCoteryFormGroup();

  constructor(
    protected coteryService: CoteryService,
    protected coteryFormService: CoteryFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cotery }) => {
      this.cotery = cotery;
      if (cotery) {
        this.updateForm(cotery);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cotery = this.coteryFormService.getCotery(this.editForm);
    if (cotery.id !== null) {
      this.subscribeToSaveResponse(this.coteryService.update(cotery));
    } else {
      this.subscribeToSaveResponse(this.coteryService.create(cotery));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICotery>>): void {
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

  protected updateForm(cotery: ICotery): void {
    this.cotery = cotery;
    this.coteryFormService.resetForm(this.editForm, cotery);
  }
}
