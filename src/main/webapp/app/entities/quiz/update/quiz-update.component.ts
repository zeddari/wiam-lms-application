import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IQuestion } from 'app/entities/question/question.model';
import { QuestionService } from 'app/entities/question/service/question.service';
import { QuizType } from 'app/entities/enumerations/quiz-type.model';
import { QuizService } from '../service/quiz.service';
import { IQuiz } from '../quiz.model';
import { QuizFormService, QuizFormGroup } from './quiz-form.service';

@Component({
  standalone: true,
  selector: 'jhi-quiz-update',
  templateUrl: './quiz-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class QuizUpdateComponent implements OnInit {
  isSaving = false;
  quiz: IQuiz | null = null;
  quizTypeValues = Object.keys(QuizType);

  questionsSharedCollection: IQuestion[] = [];

  editForm: QuizFormGroup = this.quizFormService.createQuizFormGroup();

  constructor(
    protected quizService: QuizService,
    protected quizFormService: QuizFormService,
    protected questionService: QuestionService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareQuestion = (o1: IQuestion | null, o2: IQuestion | null): boolean => this.questionService.compareQuestion(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quiz }) => {
      this.quiz = quiz;
      if (quiz) {
        this.updateForm(quiz);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const quiz = this.quizFormService.getQuiz(this.editForm);
    if (quiz.id !== null) {
      this.subscribeToSaveResponse(this.quizService.update(quiz));
    } else {
      this.subscribeToSaveResponse(this.quizService.create(quiz));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQuiz>>): void {
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

  protected updateForm(quiz: IQuiz): void {
    this.quiz = quiz;
    this.quizFormService.resetForm(this.editForm, quiz);

    this.questionsSharedCollection = this.questionService.addQuestionToCollectionIfMissing<IQuestion>(
      this.questionsSharedCollection,
      ...(quiz.questions ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.questionService
      .query()
      .pipe(map((res: HttpResponse<IQuestion[]>) => res.body ?? []))
      .pipe(
        map((questions: IQuestion[]) =>
          this.questionService.addQuestionToCollectionIfMissing<IQuestion>(questions, ...(this.quiz?.questions ?? [])),
        ),
      )
      .subscribe((questions: IQuestion[]) => (this.questionsSharedCollection = questions));
  }
}
