import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { QuestionType } from 'app/entities/enumerations/question-type.model';
import { IQuestion2 } from '../question-2.model';
import { Question2Service } from '../service/question-2.service';
import { Question2FormService, Question2FormGroup } from './question-2-form.service';

@Component({
  standalone: true,
  selector: 'jhi-question-2-update',
  templateUrl: './question-2-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class Question2UpdateComponent implements OnInit {
  isSaving = false;
  question2: IQuestion2 | null = null;
  questionTypeValues = Object.keys(QuestionType);

  editForm: Question2FormGroup = this.question2FormService.createQuestion2FormGroup();

  constructor(
    protected question2Service: Question2Service,
    protected question2FormService: Question2FormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ question2 }) => {
      this.question2 = question2;
      if (question2) {
        this.updateForm(question2);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const question2 = this.question2FormService.getQuestion2(this.editForm);
    if (question2.id !== null) {
      this.subscribeToSaveResponse(this.question2Service.update(question2));
    } else {
      this.subscribeToSaveResponse(this.question2Service.create(question2));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQuestion2>>): void {
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

  protected updateForm(question2: IQuestion2): void {
    this.question2 = question2;
    this.question2FormService.resetForm(this.editForm, question2);
  }
}
