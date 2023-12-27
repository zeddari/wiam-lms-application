import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISite } from 'app/entities/site/site.model';
import { SiteService } from 'app/entities/site/service/site.service';
import { IClassroom } from '../classroom.model';
import { ClassroomService } from '../service/classroom.service';
import { ClassroomFormService, ClassroomFormGroup } from './classroom-form.service';

@Component({
  standalone: true,
  selector: 'jhi-classroom-update',
  templateUrl: './classroom-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ClassroomUpdateComponent implements OnInit {
  isSaving = false;
  classroom: IClassroom | null = null;

  sitesSharedCollection: ISite[] = [];

  editForm: ClassroomFormGroup = this.classroomFormService.createClassroomFormGroup();

  constructor(
    protected classroomService: ClassroomService,
    protected classroomFormService: ClassroomFormService,
    protected siteService: SiteService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareSite = (o1: ISite | null, o2: ISite | null): boolean => this.siteService.compareSite(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ classroom }) => {
      this.classroom = classroom;
      if (classroom) {
        this.updateForm(classroom);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const classroom = this.classroomFormService.getClassroom(this.editForm);
    if (classroom.id !== null) {
      this.subscribeToSaveResponse(this.classroomService.update(classroom));
    } else {
      this.subscribeToSaveResponse(this.classroomService.create(classroom));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClassroom>>): void {
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

  protected updateForm(classroom: IClassroom): void {
    this.classroom = classroom;
    this.classroomFormService.resetForm(this.editForm, classroom);

    this.sitesSharedCollection = this.siteService.addSiteToCollectionIfMissing<ISite>(this.sitesSharedCollection, classroom.site);
  }

  protected loadRelationshipsOptions(): void {
    this.siteService
      .query()
      .pipe(map((res: HttpResponse<ISite[]>) => res.body ?? []))
      .pipe(map((sites: ISite[]) => this.siteService.addSiteToCollectionIfMissing<ISite>(sites, this.classroom?.site)))
      .subscribe((sites: ISite[]) => (this.sitesSharedCollection = sites));
  }
}
