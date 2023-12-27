import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { IQuestion } from 'app/entities/question/question.model';
import { QuestionService } from 'app/entities/question/service/question.service';
import { IPart } from 'app/entities/part/part.model';
import { PartService } from 'app/entities/part/service/part.service';
import { ISession } from 'app/entities/session/session.model';
import { SessionService } from 'app/entities/session/service/session.service';
import { IQuizCertificateType } from 'app/entities/quiz-certificate-type/quiz-certificate-type.model';
import { QuizCertificateTypeService } from 'app/entities/quiz-certificate-type/service/quiz-certificate-type.service';
import { QuizCertificateService } from '../service/quiz-certificate.service';
import { IQuizCertificate } from '../quiz-certificate.model';
import { QuizCertificateFormService, QuizCertificateFormGroup } from './quiz-certificate-form.service';

@Component({
  standalone: true,
  selector: 'jhi-quiz-certificate-update',
  templateUrl: './quiz-certificate-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class QuizCertificateUpdateComponent implements OnInit {
  isSaving = false;
  quizCertificate: IQuizCertificate | null = null;

  studentsSharedCollection: IStudent[] = [];
  questionsSharedCollection: IQuestion[] = [];
  partsSharedCollection: IPart[] = [];
  sessionsSharedCollection: ISession[] = [];
  quizCertificateTypesSharedCollection: IQuizCertificateType[] = [];

  editForm: QuizCertificateFormGroup = this.quizCertificateFormService.createQuizCertificateFormGroup();

  constructor(
    protected quizCertificateService: QuizCertificateService,
    protected quizCertificateFormService: QuizCertificateFormService,
    protected studentService: StudentService,
    protected questionService: QuestionService,
    protected partService: PartService,
    protected sessionService: SessionService,
    protected quizCertificateTypeService: QuizCertificateTypeService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareStudent = (o1: IStudent | null, o2: IStudent | null): boolean => this.studentService.compareStudent(o1, o2);

  compareQuestion = (o1: IQuestion | null, o2: IQuestion | null): boolean => this.questionService.compareQuestion(o1, o2);

  comparePart = (o1: IPart | null, o2: IPart | null): boolean => this.partService.comparePart(o1, o2);

  compareSession = (o1: ISession | null, o2: ISession | null): boolean => this.sessionService.compareSession(o1, o2);

  compareQuizCertificateType = (o1: IQuizCertificateType | null, o2: IQuizCertificateType | null): boolean =>
    this.quizCertificateTypeService.compareQuizCertificateType(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quizCertificate }) => {
      this.quizCertificate = quizCertificate;
      if (quizCertificate) {
        this.updateForm(quizCertificate);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const quizCertificate = this.quizCertificateFormService.getQuizCertificate(this.editForm);
    if (quizCertificate.id !== null) {
      this.subscribeToSaveResponse(this.quizCertificateService.update(quizCertificate));
    } else {
      this.subscribeToSaveResponse(this.quizCertificateService.create(quizCertificate));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQuizCertificate>>): void {
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

  protected updateForm(quizCertificate: IQuizCertificate): void {
    this.quizCertificate = quizCertificate;
    this.quizCertificateFormService.resetForm(this.editForm, quizCertificate);

    this.studentsSharedCollection = this.studentService.addStudentToCollectionIfMissing<IStudent>(
      this.studentsSharedCollection,
      ...(quizCertificate.students ?? []),
    );
    this.questionsSharedCollection = this.questionService.addQuestionToCollectionIfMissing<IQuestion>(
      this.questionsSharedCollection,
      ...(quizCertificate.questions ?? []),
    );
    this.partsSharedCollection = this.partService.addPartToCollectionIfMissing<IPart>(this.partsSharedCollection, quizCertificate.part);
    this.sessionsSharedCollection = this.sessionService.addSessionToCollectionIfMissing<ISession>(
      this.sessionsSharedCollection,
      quizCertificate.session,
    );
    this.quizCertificateTypesSharedCollection =
      this.quizCertificateTypeService.addQuizCertificateTypeToCollectionIfMissing<IQuizCertificateType>(
        this.quizCertificateTypesSharedCollection,
        quizCertificate.type,
      );
  }

  protected loadRelationshipsOptions(): void {
    this.studentService
      .query()
      .pipe(map((res: HttpResponse<IStudent[]>) => res.body ?? []))
      .pipe(
        map((students: IStudent[]) =>
          this.studentService.addStudentToCollectionIfMissing<IStudent>(students, ...(this.quizCertificate?.students ?? [])),
        ),
      )
      .subscribe((students: IStudent[]) => (this.studentsSharedCollection = students));

    this.questionService
      .query()
      .pipe(map((res: HttpResponse<IQuestion[]>) => res.body ?? []))
      .pipe(
        map((questions: IQuestion[]) =>
          this.questionService.addQuestionToCollectionIfMissing<IQuestion>(questions, ...(this.quizCertificate?.questions ?? [])),
        ),
      )
      .subscribe((questions: IQuestion[]) => (this.questionsSharedCollection = questions));

    this.partService
      .query()
      .pipe(map((res: HttpResponse<IPart[]>) => res.body ?? []))
      .pipe(map((parts: IPart[]) => this.partService.addPartToCollectionIfMissing<IPart>(parts, this.quizCertificate?.part)))
      .subscribe((parts: IPart[]) => (this.partsSharedCollection = parts));

    this.sessionService
      .query()
      .pipe(map((res: HttpResponse<ISession[]>) => res.body ?? []))
      .pipe(
        map((sessions: ISession[]) =>
          this.sessionService.addSessionToCollectionIfMissing<ISession>(sessions, this.quizCertificate?.session),
        ),
      )
      .subscribe((sessions: ISession[]) => (this.sessionsSharedCollection = sessions));

    this.quizCertificateTypeService
      .query()
      .pipe(map((res: HttpResponse<IQuizCertificateType[]>) => res.body ?? []))
      .pipe(
        map((quizCertificateTypes: IQuizCertificateType[]) =>
          this.quizCertificateTypeService.addQuizCertificateTypeToCollectionIfMissing<IQuizCertificateType>(
            quizCertificateTypes,
            this.quizCertificate?.type,
          ),
        ),
      )
      .subscribe((quizCertificateTypes: IQuizCertificateType[]) => (this.quizCertificateTypesSharedCollection = quizCertificateTypes));
  }
}
