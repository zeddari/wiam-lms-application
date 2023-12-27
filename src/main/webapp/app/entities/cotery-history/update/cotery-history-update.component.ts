import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ICotery } from 'app/entities/cotery/cotery.model';
import { CoteryService } from 'app/entities/cotery/service/cotery.service';
import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { Attendance } from 'app/entities/enumerations/attendance.model';
import { CoteryHistoryService } from '../service/cotery-history.service';
import { ICoteryHistory } from '../cotery-history.model';
import { CoteryHistoryFormService, CoteryHistoryFormGroup } from './cotery-history-form.service';

@Component({
  standalone: true,
  selector: 'jhi-cotery-history-update',
  templateUrl: './cotery-history-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CoteryHistoryUpdateComponent implements OnInit {
  isSaving = false;
  coteryHistory: ICoteryHistory | null = null;
  attendanceValues = Object.keys(Attendance);

  coteriesSharedCollection: ICotery[] = [];
  studentsSharedCollection: IStudent[] = [];

  editForm: CoteryHistoryFormGroup = this.coteryHistoryFormService.createCoteryHistoryFormGroup();

  constructor(
    protected coteryHistoryService: CoteryHistoryService,
    protected coteryHistoryFormService: CoteryHistoryFormService,
    protected coteryService: CoteryService,
    protected studentService: StudentService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareCotery = (o1: ICotery | null, o2: ICotery | null): boolean => this.coteryService.compareCotery(o1, o2);

  compareStudent = (o1: IStudent | null, o2: IStudent | null): boolean => this.studentService.compareStudent(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ coteryHistory }) => {
      this.coteryHistory = coteryHistory;
      if (coteryHistory) {
        this.updateForm(coteryHistory);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const coteryHistory = this.coteryHistoryFormService.getCoteryHistory(this.editForm);
    if (coteryHistory.id !== null) {
      this.subscribeToSaveResponse(this.coteryHistoryService.update(coteryHistory));
    } else {
      this.subscribeToSaveResponse(this.coteryHistoryService.create(coteryHistory));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICoteryHistory>>): void {
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

  protected updateForm(coteryHistory: ICoteryHistory): void {
    this.coteryHistory = coteryHistory;
    this.coteryHistoryFormService.resetForm(this.editForm, coteryHistory);

    this.coteriesSharedCollection = this.coteryService.addCoteryToCollectionIfMissing<ICotery>(
      this.coteriesSharedCollection,
      coteryHistory.student2,
    );
    this.studentsSharedCollection = this.studentService.addStudentToCollectionIfMissing<IStudent>(
      this.studentsSharedCollection,
      coteryHistory.student,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.coteryService
      .query()
      .pipe(map((res: HttpResponse<ICotery[]>) => res.body ?? []))
      .pipe(
        map((coteries: ICotery[]) => this.coteryService.addCoteryToCollectionIfMissing<ICotery>(coteries, this.coteryHistory?.student2)),
      )
      .subscribe((coteries: ICotery[]) => (this.coteriesSharedCollection = coteries));

    this.studentService
      .query()
      .pipe(map((res: HttpResponse<IStudent[]>) => res.body ?? []))
      .pipe(
        map((students: IStudent[]) => this.studentService.addStudentToCollectionIfMissing<IStudent>(students, this.coteryHistory?.student)),
      )
      .subscribe((students: IStudent[]) => (this.studentsSharedCollection = students));
  }
}
