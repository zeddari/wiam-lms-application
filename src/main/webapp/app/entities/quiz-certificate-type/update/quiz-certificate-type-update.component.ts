import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IQuizCertificateType } from '../quiz-certificate-type.model';
import { QuizCertificateTypeService } from '../service/quiz-certificate-type.service';
import { QuizCertificateTypeFormService, QuizCertificateTypeFormGroup } from './quiz-certificate-type-form.service';

@Component({
  standalone: true,
  selector: 'jhi-quiz-certificate-type-update',
  templateUrl: './quiz-certificate-type-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class QuizCertificateTypeUpdateComponent implements OnInit {
  isSaving = false;
  quizCertificateType: IQuizCertificateType | null = null;

  editForm: QuizCertificateTypeFormGroup = this.quizCertificateTypeFormService.createQuizCertificateTypeFormGroup();

  constructor(
    protected quizCertificateTypeService: QuizCertificateTypeService,
    protected quizCertificateTypeFormService: QuizCertificateTypeFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quizCertificateType }) => {
      this.quizCertificateType = quizCertificateType;
      if (quizCertificateType) {
        this.updateForm(quizCertificateType);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const quizCertificateType = this.quizCertificateTypeFormService.getQuizCertificateType(this.editForm);
    if (quizCertificateType.id !== null) {
      this.subscribeToSaveResponse(this.quizCertificateTypeService.update(quizCertificateType));
    } else {
      this.subscribeToSaveResponse(this.quizCertificateTypeService.create(quizCertificateType));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQuizCertificateType>>): void {
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

  protected updateForm(quizCertificateType: IQuizCertificateType): void {
    this.quizCertificateType = quizCertificateType;
    this.quizCertificateTypeFormService.resetForm(this.editForm, quizCertificateType);
  }
}
