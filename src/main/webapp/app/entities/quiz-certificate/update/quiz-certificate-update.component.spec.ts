import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

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
import { IQuizCertificate } from '../quiz-certificate.model';
import { QuizCertificateService } from '../service/quiz-certificate.service';
import { QuizCertificateFormService } from './quiz-certificate-form.service';

import { QuizCertificateUpdateComponent } from './quiz-certificate-update.component';

describe('QuizCertificate Management Update Component', () => {
  let comp: QuizCertificateUpdateComponent;
  let fixture: ComponentFixture<QuizCertificateUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let quizCertificateFormService: QuizCertificateFormService;
  let quizCertificateService: QuizCertificateService;
  let studentService: StudentService;
  let questionService: QuestionService;
  let partService: PartService;
  let sessionService: SessionService;
  let quizCertificateTypeService: QuizCertificateTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), QuizCertificateUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(QuizCertificateUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuizCertificateUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    quizCertificateFormService = TestBed.inject(QuizCertificateFormService);
    quizCertificateService = TestBed.inject(QuizCertificateService);
    studentService = TestBed.inject(StudentService);
    questionService = TestBed.inject(QuestionService);
    partService = TestBed.inject(PartService);
    sessionService = TestBed.inject(SessionService);
    quizCertificateTypeService = TestBed.inject(QuizCertificateTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Student query and add missing value', () => {
      const quizCertificate: IQuizCertificate = { id: 456 };
      const students: IStudent[] = [{ id: 9383 }];
      quizCertificate.students = students;

      const studentCollection: IStudent[] = [{ id: 3178 }];
      jest.spyOn(studentService, 'query').mockReturnValue(of(new HttpResponse({ body: studentCollection })));
      const additionalStudents = [...students];
      const expectedCollection: IStudent[] = [...additionalStudents, ...studentCollection];
      jest.spyOn(studentService, 'addStudentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ quizCertificate });
      comp.ngOnInit();

      expect(studentService.query).toHaveBeenCalled();
      expect(studentService.addStudentToCollectionIfMissing).toHaveBeenCalledWith(
        studentCollection,
        ...additionalStudents.map(expect.objectContaining),
      );
      expect(comp.studentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Question query and add missing value', () => {
      const quizCertificate: IQuizCertificate = { id: 456 };
      const questions: IQuestion[] = [{ id: 7630 }];
      quizCertificate.questions = questions;

      const questionCollection: IQuestion[] = [{ id: 29413 }];
      jest.spyOn(questionService, 'query').mockReturnValue(of(new HttpResponse({ body: questionCollection })));
      const additionalQuestions = [...questions];
      const expectedCollection: IQuestion[] = [...additionalQuestions, ...questionCollection];
      jest.spyOn(questionService, 'addQuestionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ quizCertificate });
      comp.ngOnInit();

      expect(questionService.query).toHaveBeenCalled();
      expect(questionService.addQuestionToCollectionIfMissing).toHaveBeenCalledWith(
        questionCollection,
        ...additionalQuestions.map(expect.objectContaining),
      );
      expect(comp.questionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Part query and add missing value', () => {
      const quizCertificate: IQuizCertificate = { id: 456 };
      const part: IPart = { id: 28684 };
      quizCertificate.part = part;

      const partCollection: IPart[] = [{ id: 28804 }];
      jest.spyOn(partService, 'query').mockReturnValue(of(new HttpResponse({ body: partCollection })));
      const additionalParts = [part];
      const expectedCollection: IPart[] = [...additionalParts, ...partCollection];
      jest.spyOn(partService, 'addPartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ quizCertificate });
      comp.ngOnInit();

      expect(partService.query).toHaveBeenCalled();
      expect(partService.addPartToCollectionIfMissing).toHaveBeenCalledWith(
        partCollection,
        ...additionalParts.map(expect.objectContaining),
      );
      expect(comp.partsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Session query and add missing value', () => {
      const quizCertificate: IQuizCertificate = { id: 456 };
      const session: ISession = { id: 14793 };
      quizCertificate.session = session;

      const sessionCollection: ISession[] = [{ id: 28616 }];
      jest.spyOn(sessionService, 'query').mockReturnValue(of(new HttpResponse({ body: sessionCollection })));
      const additionalSessions = [session];
      const expectedCollection: ISession[] = [...additionalSessions, ...sessionCollection];
      jest.spyOn(sessionService, 'addSessionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ quizCertificate });
      comp.ngOnInit();

      expect(sessionService.query).toHaveBeenCalled();
      expect(sessionService.addSessionToCollectionIfMissing).toHaveBeenCalledWith(
        sessionCollection,
        ...additionalSessions.map(expect.objectContaining),
      );
      expect(comp.sessionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call QuizCertificateType query and add missing value', () => {
      const quizCertificate: IQuizCertificate = { id: 456 };
      const type: IQuizCertificateType = { id: 2659 };
      quizCertificate.type = type;

      const quizCertificateTypeCollection: IQuizCertificateType[] = [{ id: 15525 }];
      jest.spyOn(quizCertificateTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: quizCertificateTypeCollection })));
      const additionalQuizCertificateTypes = [type];
      const expectedCollection: IQuizCertificateType[] = [...additionalQuizCertificateTypes, ...quizCertificateTypeCollection];
      jest.spyOn(quizCertificateTypeService, 'addQuizCertificateTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ quizCertificate });
      comp.ngOnInit();

      expect(quizCertificateTypeService.query).toHaveBeenCalled();
      expect(quizCertificateTypeService.addQuizCertificateTypeToCollectionIfMissing).toHaveBeenCalledWith(
        quizCertificateTypeCollection,
        ...additionalQuizCertificateTypes.map(expect.objectContaining),
      );
      expect(comp.quizCertificateTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const quizCertificate: IQuizCertificate = { id: 456 };
      const students: IStudent = { id: 26239 };
      quizCertificate.students = [students];
      const questions: IQuestion = { id: 23204 };
      quizCertificate.questions = [questions];
      const part: IPart = { id: 9620 };
      quizCertificate.part = part;
      const session: ISession = { id: 22259 };
      quizCertificate.session = session;
      const type: IQuizCertificateType = { id: 13105 };
      quizCertificate.type = type;

      activatedRoute.data = of({ quizCertificate });
      comp.ngOnInit();

      expect(comp.studentsSharedCollection).toContain(students);
      expect(comp.questionsSharedCollection).toContain(questions);
      expect(comp.partsSharedCollection).toContain(part);
      expect(comp.sessionsSharedCollection).toContain(session);
      expect(comp.quizCertificateTypesSharedCollection).toContain(type);
      expect(comp.quizCertificate).toEqual(quizCertificate);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuizCertificate>>();
      const quizCertificate = { id: 123 };
      jest.spyOn(quizCertificateFormService, 'getQuizCertificate').mockReturnValue(quizCertificate);
      jest.spyOn(quizCertificateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quizCertificate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quizCertificate }));
      saveSubject.complete();

      // THEN
      expect(quizCertificateFormService.getQuizCertificate).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(quizCertificateService.update).toHaveBeenCalledWith(expect.objectContaining(quizCertificate));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuizCertificate>>();
      const quizCertificate = { id: 123 };
      jest.spyOn(quizCertificateFormService, 'getQuizCertificate').mockReturnValue({ id: null });
      jest.spyOn(quizCertificateService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quizCertificate: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quizCertificate }));
      saveSubject.complete();

      // THEN
      expect(quizCertificateFormService.getQuizCertificate).toHaveBeenCalled();
      expect(quizCertificateService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuizCertificate>>();
      const quizCertificate = { id: 123 };
      jest.spyOn(quizCertificateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quizCertificate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(quizCertificateService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStudent', () => {
      it('Should forward to studentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(studentService, 'compareStudent');
        comp.compareStudent(entity, entity2);
        expect(studentService.compareStudent).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareQuestion', () => {
      it('Should forward to questionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(questionService, 'compareQuestion');
        comp.compareQuestion(entity, entity2);
        expect(questionService.compareQuestion).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePart', () => {
      it('Should forward to partService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(partService, 'comparePart');
        comp.comparePart(entity, entity2);
        expect(partService.comparePart).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSession', () => {
      it('Should forward to sessionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sessionService, 'compareSession');
        comp.compareSession(entity, entity2);
        expect(sessionService.compareSession).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareQuizCertificateType', () => {
      it('Should forward to quizCertificateTypeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(quizCertificateTypeService, 'compareQuizCertificateType');
        comp.compareQuizCertificateType(entity, entity2);
        expect(quizCertificateTypeService.compareQuizCertificateType).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
