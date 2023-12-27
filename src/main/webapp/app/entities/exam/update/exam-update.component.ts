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
import { Riwayats } from 'app/entities/enumerations/riwayats.model';
import { ExamType } from 'app/entities/enumerations/exam-type.model';
import { ExamService } from '../service/exam.service';
import { IExam } from '../exam.model';
import { ExamFormService, ExamFormGroup } from './exam-form.service';

@Component({
  standalone: true,
  selector: 'jhi-exam-update',
  templateUrl: './exam-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ExamUpdateComponent implements OnInit {
  isSaving = false;
  exam: IExam | null = null;
  riwayatsValues = Object.keys(Riwayats);
  examTypeValues = Object.keys(ExamType);

  editForm: ExamFormGroup = this.examFormService.createExamFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected examService: ExamService,
    protected examFormService: ExamFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ exam }) => {
      this.exam = exam;
      if (exam) {
        this.updateForm(exam);
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
    const exam = this.examFormService.getExam(this.editForm);
    if (exam.id !== null) {
      this.subscribeToSaveResponse(this.examService.update(exam));
    } else {
      this.subscribeToSaveResponse(this.examService.create(exam));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExam>>): void {
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

  protected updateForm(exam: IExam): void {
    this.exam = exam;
    this.examFormService.resetForm(this.editForm, exam);
  }
}
