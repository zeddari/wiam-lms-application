import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { UserCustomService } from 'app/entities/user-custom/service/user-custom.service';
import { IDiplomaType } from 'app/entities/diploma-type/diploma-type.model';
import { DiplomaTypeService } from 'app/entities/diploma-type/service/diploma-type.service';
import { DiplomaService } from '../service/diploma.service';
import { IDiploma } from '../diploma.model';
import { DiplomaFormService, DiplomaFormGroup } from './diploma-form.service';

@Component({
  standalone: true,
  selector: 'jhi-diploma-update',
  templateUrl: './diploma-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DiplomaUpdateComponent implements OnInit {
  isSaving = false;
  diploma: IDiploma | null = null;

  userCustomsSharedCollection: IUserCustom[] = [];
  diplomaTypesSharedCollection: IDiplomaType[] = [];

  editForm: DiplomaFormGroup = this.diplomaFormService.createDiplomaFormGroup();

  constructor(
    protected diplomaService: DiplomaService,
    protected diplomaFormService: DiplomaFormService,
    protected userCustomService: UserCustomService,
    protected diplomaTypeService: DiplomaTypeService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareUserCustom = (o1: IUserCustom | null, o2: IUserCustom | null): boolean => this.userCustomService.compareUserCustom(o1, o2);

  compareDiplomaType = (o1: IDiplomaType | null, o2: IDiplomaType | null): boolean => this.diplomaTypeService.compareDiplomaType(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ diploma }) => {
      this.diploma = diploma;
      if (diploma) {
        this.updateForm(diploma);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const diploma = this.diplomaFormService.getDiploma(this.editForm);
    if (diploma.id !== null) {
      this.subscribeToSaveResponse(this.diplomaService.update(diploma));
    } else {
      this.subscribeToSaveResponse(this.diplomaService.create(diploma));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDiploma>>): void {
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

  protected updateForm(diploma: IDiploma): void {
    this.diploma = diploma;
    this.diplomaFormService.resetForm(this.editForm, diploma);

    this.userCustomsSharedCollection = this.userCustomService.addUserCustomToCollectionIfMissing<IUserCustom>(
      this.userCustomsSharedCollection,
      diploma.userCustom,
    );
    this.diplomaTypesSharedCollection = this.diplomaTypeService.addDiplomaTypeToCollectionIfMissing<IDiplomaType>(
      this.diplomaTypesSharedCollection,
      diploma.diplomaType,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userCustomService
      .query()
      .pipe(map((res: HttpResponse<IUserCustom[]>) => res.body ?? []))
      .pipe(
        map((userCustoms: IUserCustom[]) =>
          this.userCustomService.addUserCustomToCollectionIfMissing<IUserCustom>(userCustoms, this.diploma?.userCustom),
        ),
      )
      .subscribe((userCustoms: IUserCustom[]) => (this.userCustomsSharedCollection = userCustoms));

    this.diplomaTypeService
      .query()
      .pipe(map((res: HttpResponse<IDiplomaType[]>) => res.body ?? []))
      .pipe(
        map((diplomaTypes: IDiplomaType[]) =>
          this.diplomaTypeService.addDiplomaTypeToCollectionIfMissing<IDiplomaType>(diplomaTypes, this.diploma?.diplomaType),
        ),
      )
      .subscribe((diplomaTypes: IDiplomaType[]) => (this.diplomaTypesSharedCollection = diplomaTypes));
  }
}
