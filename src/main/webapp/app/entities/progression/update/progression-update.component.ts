import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ISession } from 'app/entities/session/session.model';
import { SessionService } from 'app/entities/session/service/session.service';
import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { IProgressionMode } from 'app/entities/progression-mode/progression-mode.model';
import { ProgressionModeService } from 'app/entities/progression-mode/service/progression-mode.service';
import { ProgressionService } from '../service/progression.service';
import { IProgression } from '../progression.model';
import { ProgressionFormService, ProgressionFormGroup } from './progression-form.service';

@Component({
  standalone: true,
  selector: 'jhi-progression-update',
  templateUrl: './progression-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ProgressionUpdateComponent implements OnInit {
  isSaving = false;
  progression: IProgression | null = null;

  sessionsSharedCollection: ISession[] = [];
  studentsSharedCollection: IStudent[] = [];
  progressionModesSharedCollection: IProgressionMode[] = [];

  editForm: ProgressionFormGroup = this.progressionFormService.createProgressionFormGroup();

  constructor(
    protected progressionService: ProgressionService,
    protected progressionFormService: ProgressionFormService,
    protected sessionService: SessionService,
    protected studentService: StudentService,
    protected progressionModeService: ProgressionModeService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareSession = (o1: ISession | null, o2: ISession | null): boolean => this.sessionService.compareSession(o1, o2);

  compareStudent = (o1: IStudent | null, o2: IStudent | null): boolean => this.studentService.compareStudent(o1, o2);

  compareProgressionMode = (o1: IProgressionMode | null, o2: IProgressionMode | null): boolean =>
    this.progressionModeService.compareProgressionMode(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ progression }) => {
      this.progression = progression;
      if (progression) {
        this.updateForm(progression);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const progression = this.progressionFormService.getProgression(this.editForm);
    if (progression.id !== null) {
      this.subscribeToSaveResponse(this.progressionService.update(progression));
    } else {
      this.subscribeToSaveResponse(this.progressionService.create(progression));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProgression>>): void {
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

  protected updateForm(progression: IProgression): void {
    this.progression = progression;
    this.progressionFormService.resetForm(this.editForm, progression);

    this.sessionsSharedCollection = this.sessionService.addSessionToCollectionIfMissing<ISession>(
      this.sessionsSharedCollection,
      progression.session,
    );
    this.studentsSharedCollection = this.studentService.addStudentToCollectionIfMissing<IStudent>(
      this.studentsSharedCollection,
      progression.student1,
    );
    this.progressionModesSharedCollection = this.progressionModeService.addProgressionModeToCollectionIfMissing<IProgressionMode>(
      this.progressionModesSharedCollection,
      progression.mode,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.sessionService
      .query()
      .pipe(map((res: HttpResponse<ISession[]>) => res.body ?? []))
      .pipe(
        map((sessions: ISession[]) => this.sessionService.addSessionToCollectionIfMissing<ISession>(sessions, this.progression?.session)),
      )
      .subscribe((sessions: ISession[]) => (this.sessionsSharedCollection = sessions));

    this.studentService
      .query()
      .pipe(map((res: HttpResponse<IStudent[]>) => res.body ?? []))
      .pipe(
        map((students: IStudent[]) => this.studentService.addStudentToCollectionIfMissing<IStudent>(students, this.progression?.student1)),
      )
      .subscribe((students: IStudent[]) => (this.studentsSharedCollection = students));

    this.progressionModeService
      .query()
      .pipe(map((res: HttpResponse<IProgressionMode[]>) => res.body ?? []))
      .pipe(
        map((progressionModes: IProgressionMode[]) =>
          this.progressionModeService.addProgressionModeToCollectionIfMissing<IProgressionMode>(progressionModes, this.progression?.mode),
        ),
      )
      .subscribe((progressionModes: IProgressionMode[]) => (this.progressionModesSharedCollection = progressionModes));
  }
}
