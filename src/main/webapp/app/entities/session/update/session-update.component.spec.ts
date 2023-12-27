import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IProfessor } from 'app/entities/professor/professor.model';
import { ProfessorService } from 'app/entities/professor/service/professor.service';
import { IEmployee } from 'app/entities/employee/employee.model';
import { EmployeeService } from 'app/entities/employee/service/employee.service';
import { ISessionLink } from 'app/entities/session-link/session-link.model';
import { SessionLinkService } from 'app/entities/session-link/service/session-link.service';
import { IClassroom } from 'app/entities/classroom/classroom.model';
import { ClassroomService } from 'app/entities/classroom/service/classroom.service';
import { ISessionType } from 'app/entities/session-type/session-type.model';
import { SessionTypeService } from 'app/entities/session-type/service/session-type.service';
import { ISessionMode } from 'app/entities/session-mode/session-mode.model';
import { SessionModeService } from 'app/entities/session-mode/service/session-mode.service';
import { IPart } from 'app/entities/part/part.model';
import { PartService } from 'app/entities/part/service/part.service';
import { ISessionJoinMode } from 'app/entities/session-join-mode/session-join-mode.model';
import { SessionJoinModeService } from 'app/entities/session-join-mode/service/session-join-mode.service';
import { IGroup } from 'app/entities/group/group.model';
import { GroupService } from 'app/entities/group/service/group.service';
import { ISession } from '../session.model';
import { SessionService } from '../service/session.service';
import { SessionFormService } from './session-form.service';

import { SessionUpdateComponent } from './session-update.component';

describe('Session Management Update Component', () => {
  let comp: SessionUpdateComponent;
  let fixture: ComponentFixture<SessionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sessionFormService: SessionFormService;
  let sessionService: SessionService;
  let professorService: ProfessorService;
  let employeeService: EmployeeService;
  let sessionLinkService: SessionLinkService;
  let classroomService: ClassroomService;
  let sessionTypeService: SessionTypeService;
  let sessionModeService: SessionModeService;
  let partService: PartService;
  let sessionJoinModeService: SessionJoinModeService;
  let groupService: GroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SessionUpdateComponent],
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
      .overrideTemplate(SessionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sessionFormService = TestBed.inject(SessionFormService);
    sessionService = TestBed.inject(SessionService);
    professorService = TestBed.inject(ProfessorService);
    employeeService = TestBed.inject(EmployeeService);
    sessionLinkService = TestBed.inject(SessionLinkService);
    classroomService = TestBed.inject(ClassroomService);
    sessionTypeService = TestBed.inject(SessionTypeService);
    sessionModeService = TestBed.inject(SessionModeService);
    partService = TestBed.inject(PartService);
    sessionJoinModeService = TestBed.inject(SessionJoinModeService);
    groupService = TestBed.inject(GroupService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Professor query and add missing value', () => {
      const session: ISession = { id: 456 };
      const professors: IProfessor[] = [{ id: 8969 }];
      session.professors = professors;

      const professorCollection: IProfessor[] = [{ id: 12631 }];
      jest.spyOn(professorService, 'query').mockReturnValue(of(new HttpResponse({ body: professorCollection })));
      const additionalProfessors = [...professors];
      const expectedCollection: IProfessor[] = [...additionalProfessors, ...professorCollection];
      jest.spyOn(professorService, 'addProfessorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(professorService.query).toHaveBeenCalled();
      expect(professorService.addProfessorToCollectionIfMissing).toHaveBeenCalledWith(
        professorCollection,
        ...additionalProfessors.map(expect.objectContaining),
      );
      expect(comp.professorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Employee query and add missing value', () => {
      const session: ISession = { id: 456 };
      const employees: IEmployee[] = [{ id: 8846 }];
      session.employees = employees;

      const employeeCollection: IEmployee[] = [{ id: 31376 }];
      jest.spyOn(employeeService, 'query').mockReturnValue(of(new HttpResponse({ body: employeeCollection })));
      const additionalEmployees = [...employees];
      const expectedCollection: IEmployee[] = [...additionalEmployees, ...employeeCollection];
      jest.spyOn(employeeService, 'addEmployeeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(employeeService.query).toHaveBeenCalled();
      expect(employeeService.addEmployeeToCollectionIfMissing).toHaveBeenCalledWith(
        employeeCollection,
        ...additionalEmployees.map(expect.objectContaining),
      );
      expect(comp.employeesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call SessionLink query and add missing value', () => {
      const session: ISession = { id: 456 };
      const links: ISessionLink[] = [{ id: 19510 }];
      session.links = links;

      const sessionLinkCollection: ISessionLink[] = [{ id: 25555 }];
      jest.spyOn(sessionLinkService, 'query').mockReturnValue(of(new HttpResponse({ body: sessionLinkCollection })));
      const additionalSessionLinks = [...links];
      const expectedCollection: ISessionLink[] = [...additionalSessionLinks, ...sessionLinkCollection];
      jest.spyOn(sessionLinkService, 'addSessionLinkToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(sessionLinkService.query).toHaveBeenCalled();
      expect(sessionLinkService.addSessionLinkToCollectionIfMissing).toHaveBeenCalledWith(
        sessionLinkCollection,
        ...additionalSessionLinks.map(expect.objectContaining),
      );
      expect(comp.sessionLinksSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Classroom query and add missing value', () => {
      const session: ISession = { id: 456 };
      const classroom: IClassroom = { id: 26594 };
      session.classroom = classroom;

      const classroomCollection: IClassroom[] = [{ id: 12812 }];
      jest.spyOn(classroomService, 'query').mockReturnValue(of(new HttpResponse({ body: classroomCollection })));
      const additionalClassrooms = [classroom];
      const expectedCollection: IClassroom[] = [...additionalClassrooms, ...classroomCollection];
      jest.spyOn(classroomService, 'addClassroomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(classroomService.query).toHaveBeenCalled();
      expect(classroomService.addClassroomToCollectionIfMissing).toHaveBeenCalledWith(
        classroomCollection,
        ...additionalClassrooms.map(expect.objectContaining),
      );
      expect(comp.classroomsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call SessionType query and add missing value', () => {
      const session: ISession = { id: 456 };
      const type: ISessionType = { id: 31861 };
      session.type = type;

      const sessionTypeCollection: ISessionType[] = [{ id: 7329 }];
      jest.spyOn(sessionTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: sessionTypeCollection })));
      const additionalSessionTypes = [type];
      const expectedCollection: ISessionType[] = [...additionalSessionTypes, ...sessionTypeCollection];
      jest.spyOn(sessionTypeService, 'addSessionTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(sessionTypeService.query).toHaveBeenCalled();
      expect(sessionTypeService.addSessionTypeToCollectionIfMissing).toHaveBeenCalledWith(
        sessionTypeCollection,
        ...additionalSessionTypes.map(expect.objectContaining),
      );
      expect(comp.sessionTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call SessionMode query and add missing value', () => {
      const session: ISession = { id: 456 };
      const mode: ISessionMode = { id: 18827 };
      session.mode = mode;

      const sessionModeCollection: ISessionMode[] = [{ id: 27747 }];
      jest.spyOn(sessionModeService, 'query').mockReturnValue(of(new HttpResponse({ body: sessionModeCollection })));
      const additionalSessionModes = [mode];
      const expectedCollection: ISessionMode[] = [...additionalSessionModes, ...sessionModeCollection];
      jest.spyOn(sessionModeService, 'addSessionModeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(sessionModeService.query).toHaveBeenCalled();
      expect(sessionModeService.addSessionModeToCollectionIfMissing).toHaveBeenCalledWith(
        sessionModeCollection,
        ...additionalSessionModes.map(expect.objectContaining),
      );
      expect(comp.sessionModesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Part query and add missing value', () => {
      const session: ISession = { id: 456 };
      const part: IPart = { id: 8526 };
      session.part = part;

      const partCollection: IPart[] = [{ id: 12041 }];
      jest.spyOn(partService, 'query').mockReturnValue(of(new HttpResponse({ body: partCollection })));
      const additionalParts = [part];
      const expectedCollection: IPart[] = [...additionalParts, ...partCollection];
      jest.spyOn(partService, 'addPartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(partService.query).toHaveBeenCalled();
      expect(partService.addPartToCollectionIfMissing).toHaveBeenCalledWith(
        partCollection,
        ...additionalParts.map(expect.objectContaining),
      );
      expect(comp.partsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call SessionJoinMode query and add missing value', () => {
      const session: ISession = { id: 456 };
      const jmode: ISessionJoinMode = { id: 22836 };
      session.jmode = jmode;

      const sessionJoinModeCollection: ISessionJoinMode[] = [{ id: 21054 }];
      jest.spyOn(sessionJoinModeService, 'query').mockReturnValue(of(new HttpResponse({ body: sessionJoinModeCollection })));
      const additionalSessionJoinModes = [jmode];
      const expectedCollection: ISessionJoinMode[] = [...additionalSessionJoinModes, ...sessionJoinModeCollection];
      jest.spyOn(sessionJoinModeService, 'addSessionJoinModeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(sessionJoinModeService.query).toHaveBeenCalled();
      expect(sessionJoinModeService.addSessionJoinModeToCollectionIfMissing).toHaveBeenCalledWith(
        sessionJoinModeCollection,
        ...additionalSessionJoinModes.map(expect.objectContaining),
      );
      expect(comp.sessionJoinModesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Group query and add missing value', () => {
      const session: ISession = { id: 456 };
      const group: IGroup = { id: 25078 };
      session.group = group;

      const groupCollection: IGroup[] = [{ id: 17230 }];
      jest.spyOn(groupService, 'query').mockReturnValue(of(new HttpResponse({ body: groupCollection })));
      const additionalGroups = [group];
      const expectedCollection: IGroup[] = [...additionalGroups, ...groupCollection];
      jest.spyOn(groupService, 'addGroupToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(groupService.query).toHaveBeenCalled();
      expect(groupService.addGroupToCollectionIfMissing).toHaveBeenCalledWith(
        groupCollection,
        ...additionalGroups.map(expect.objectContaining),
      );
      expect(comp.groupsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const session: ISession = { id: 456 };
      const professors: IProfessor = { id: 19721 };
      session.professors = [professors];
      const employees: IEmployee = { id: 541 };
      session.employees = [employees];
      const links: ISessionLink = { id: 31893 };
      session.links = [links];
      const classroom: IClassroom = { id: 25161 };
      session.classroom = classroom;
      const type: ISessionType = { id: 29438 };
      session.type = type;
      const mode: ISessionMode = { id: 5936 };
      session.mode = mode;
      const part: IPart = { id: 28171 };
      session.part = part;
      const jmode: ISessionJoinMode = { id: 18574 };
      session.jmode = jmode;
      const group: IGroup = { id: 11220 };
      session.group = group;

      activatedRoute.data = of({ session });
      comp.ngOnInit();

      expect(comp.professorsSharedCollection).toContain(professors);
      expect(comp.employeesSharedCollection).toContain(employees);
      expect(comp.sessionLinksSharedCollection).toContain(links);
      expect(comp.classroomsSharedCollection).toContain(classroom);
      expect(comp.sessionTypesSharedCollection).toContain(type);
      expect(comp.sessionModesSharedCollection).toContain(mode);
      expect(comp.partsSharedCollection).toContain(part);
      expect(comp.sessionJoinModesSharedCollection).toContain(jmode);
      expect(comp.groupsSharedCollection).toContain(group);
      expect(comp.session).toEqual(session);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISession>>();
      const session = { id: 123 };
      jest.spyOn(sessionFormService, 'getSession').mockReturnValue(session);
      jest.spyOn(sessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ session });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: session }));
      saveSubject.complete();

      // THEN
      expect(sessionFormService.getSession).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sessionService.update).toHaveBeenCalledWith(expect.objectContaining(session));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISession>>();
      const session = { id: 123 };
      jest.spyOn(sessionFormService, 'getSession').mockReturnValue({ id: null });
      jest.spyOn(sessionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ session: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: session }));
      saveSubject.complete();

      // THEN
      expect(sessionFormService.getSession).toHaveBeenCalled();
      expect(sessionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISession>>();
      const session = { id: 123 };
      jest.spyOn(sessionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ session });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sessionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProfessor', () => {
      it('Should forward to professorService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(professorService, 'compareProfessor');
        comp.compareProfessor(entity, entity2);
        expect(professorService.compareProfessor).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareEmployee', () => {
      it('Should forward to employeeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(employeeService, 'compareEmployee');
        comp.compareEmployee(entity, entity2);
        expect(employeeService.compareEmployee).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSessionLink', () => {
      it('Should forward to sessionLinkService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sessionLinkService, 'compareSessionLink');
        comp.compareSessionLink(entity, entity2);
        expect(sessionLinkService.compareSessionLink).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareClassroom', () => {
      it('Should forward to classroomService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(classroomService, 'compareClassroom');
        comp.compareClassroom(entity, entity2);
        expect(classroomService.compareClassroom).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSessionType', () => {
      it('Should forward to sessionTypeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sessionTypeService, 'compareSessionType');
        comp.compareSessionType(entity, entity2);
        expect(sessionTypeService.compareSessionType).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSessionMode', () => {
      it('Should forward to sessionModeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sessionModeService, 'compareSessionMode');
        comp.compareSessionMode(entity, entity2);
        expect(sessionModeService.compareSessionMode).toHaveBeenCalledWith(entity, entity2);
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

    describe('compareSessionJoinMode', () => {
      it('Should forward to sessionJoinModeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sessionJoinModeService, 'compareSessionJoinMode');
        comp.compareSessionJoinMode(entity, entity2);
        expect(sessionJoinModeService.compareSessionJoinMode).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareGroup', () => {
      it('Should forward to groupService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(groupService, 'compareGroup');
        comp.compareGroup(entity, entity2);
        expect(groupService.compareGroup).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
