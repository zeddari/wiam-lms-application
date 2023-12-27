import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ISession } from 'app/entities/session/session.model';
import { SessionService } from 'app/entities/session/service/session.service';
import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { IProgressionMode } from 'app/entities/progression-mode/progression-mode.model';
import { ProgressionModeService } from 'app/entities/progression-mode/service/progression-mode.service';
import { IProgression } from '../progression.model';
import { ProgressionService } from '../service/progression.service';
import { ProgressionFormService } from './progression-form.service';

import { ProgressionUpdateComponent } from './progression-update.component';

describe('Progression Management Update Component', () => {
  let comp: ProgressionUpdateComponent;
  let fixture: ComponentFixture<ProgressionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let progressionFormService: ProgressionFormService;
  let progressionService: ProgressionService;
  let sessionService: SessionService;
  let studentService: StudentService;
  let progressionModeService: ProgressionModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ProgressionUpdateComponent],
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
      .overrideTemplate(ProgressionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgressionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    progressionFormService = TestBed.inject(ProgressionFormService);
    progressionService = TestBed.inject(ProgressionService);
    sessionService = TestBed.inject(SessionService);
    studentService = TestBed.inject(StudentService);
    progressionModeService = TestBed.inject(ProgressionModeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Session query and add missing value', () => {
      const progression: IProgression = { id: 456 };
      const session: ISession = { id: 3409 };
      progression.session = session;

      const sessionCollection: ISession[] = [{ id: 2368 }];
      jest.spyOn(sessionService, 'query').mockReturnValue(of(new HttpResponse({ body: sessionCollection })));
      const additionalSessions = [session];
      const expectedCollection: ISession[] = [...additionalSessions, ...sessionCollection];
      jest.spyOn(sessionService, 'addSessionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ progression });
      comp.ngOnInit();

      expect(sessionService.query).toHaveBeenCalled();
      expect(sessionService.addSessionToCollectionIfMissing).toHaveBeenCalledWith(
        sessionCollection,
        ...additionalSessions.map(expect.objectContaining),
      );
      expect(comp.sessionsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Student query and add missing value', () => {
      const progression: IProgression = { id: 456 };
      const student1: IStudent = { id: 21578 };
      progression.student1 = student1;

      const studentCollection: IStudent[] = [{ id: 7266 }];
      jest.spyOn(studentService, 'query').mockReturnValue(of(new HttpResponse({ body: studentCollection })));
      const additionalStudents = [student1];
      const expectedCollection: IStudent[] = [...additionalStudents, ...studentCollection];
      jest.spyOn(studentService, 'addStudentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ progression });
      comp.ngOnInit();

      expect(studentService.query).toHaveBeenCalled();
      expect(studentService.addStudentToCollectionIfMissing).toHaveBeenCalledWith(
        studentCollection,
        ...additionalStudents.map(expect.objectContaining),
      );
      expect(comp.studentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ProgressionMode query and add missing value', () => {
      const progression: IProgression = { id: 456 };
      const mode: IProgressionMode = { id: 17699 };
      progression.mode = mode;

      const progressionModeCollection: IProgressionMode[] = [{ id: 31272 }];
      jest.spyOn(progressionModeService, 'query').mockReturnValue(of(new HttpResponse({ body: progressionModeCollection })));
      const additionalProgressionModes = [mode];
      const expectedCollection: IProgressionMode[] = [...additionalProgressionModes, ...progressionModeCollection];
      jest.spyOn(progressionModeService, 'addProgressionModeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ progression });
      comp.ngOnInit();

      expect(progressionModeService.query).toHaveBeenCalled();
      expect(progressionModeService.addProgressionModeToCollectionIfMissing).toHaveBeenCalledWith(
        progressionModeCollection,
        ...additionalProgressionModes.map(expect.objectContaining),
      );
      expect(comp.progressionModesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const progression: IProgression = { id: 456 };
      const session: ISession = { id: 26475 };
      progression.session = session;
      const student1: IStudent = { id: 48 };
      progression.student1 = student1;
      const mode: IProgressionMode = { id: 7891 };
      progression.mode = mode;

      activatedRoute.data = of({ progression });
      comp.ngOnInit();

      expect(comp.sessionsSharedCollection).toContain(session);
      expect(comp.studentsSharedCollection).toContain(student1);
      expect(comp.progressionModesSharedCollection).toContain(mode);
      expect(comp.progression).toEqual(progression);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgression>>();
      const progression = { id: 123 };
      jest.spyOn(progressionFormService, 'getProgression').mockReturnValue(progression);
      jest.spyOn(progressionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ progression });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: progression }));
      saveSubject.complete();

      // THEN
      expect(progressionFormService.getProgression).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(progressionService.update).toHaveBeenCalledWith(expect.objectContaining(progression));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgression>>();
      const progression = { id: 123 };
      jest.spyOn(progressionFormService, 'getProgression').mockReturnValue({ id: null });
      jest.spyOn(progressionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ progression: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: progression }));
      saveSubject.complete();

      // THEN
      expect(progressionFormService.getProgression).toHaveBeenCalled();
      expect(progressionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgression>>();
      const progression = { id: 123 };
      jest.spyOn(progressionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ progression });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(progressionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSession', () => {
      it('Should forward to sessionService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sessionService, 'compareSession');
        comp.compareSession(entity, entity2);
        expect(sessionService.compareSession).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStudent', () => {
      it('Should forward to studentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(studentService, 'compareStudent');
        comp.compareStudent(entity, entity2);
        expect(studentService.compareStudent).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProgressionMode', () => {
      it('Should forward to progressionModeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(progressionModeService, 'compareProgressionMode');
        comp.compareProgressionMode(entity, entity2);
        expect(progressionModeService.compareProgressionMode).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
