import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDiplomaType } from '../diploma-type.model';
import { DiplomaTypeService } from '../service/diploma-type.service';
import { DiplomaTypeFormService, DiplomaTypeFormGroup } from './diploma-type-form.service';

@Component({
  standalone: true,
  selector: 'jhi-diploma-type-update',
  templateUrl: './diploma-type-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DiplomaTypeUpdateComponent implements OnInit {
  isSaving = false;
  diplomaType: IDiplomaType | null = null;

  editForm: DiplomaTypeFormGroup = this.diplomaTypeFormService.createDiplomaTypeFormGroup();

  constructor(
    protected diplomaTypeService: DiplomaTypeService,
    protected diplomaTypeFormService: DiplomaTypeFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ diplomaType }) => {
      this.diplomaType = diplomaType;
      if (diplomaType) {
        this.updateForm(diplomaType);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const diplomaType = this.diplomaTypeFormService.getDiplomaType(this.editForm);
    if (diplomaType.id !== null) {
      this.subscribeToSaveResponse(this.diplomaTypeService.update(diplomaType));
    } else {
      this.subscribeToSaveResponse(this.diplomaTypeService.create(diplomaType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDiplomaType>>): void {
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

  protected updateForm(diplomaType: IDiplomaType): void {
    this.diplomaType = diplomaType;
    this.diplomaTypeFormService.resetForm(this.editForm, diplomaType);
  }
}
