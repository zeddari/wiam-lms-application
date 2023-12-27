import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
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
import { SessionService } from '../service/session.service';
import { ISession } from '../session.model';
import { SessionFormService, SessionFormGroup } from './session-form.service';

@Component({
  standalone: true,
  selector: 'jhi-session-update',
  templateUrl: './session-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class SessionUpdateComponent implements OnInit {
  isSaving = false;
  session: ISession | null = null;

  professorsSharedCollection: IProfessor[] = [];
  employeesSharedCollection: IEmployee[] = [];
  sessionLinksSharedCollection: ISessionLink[] = [];
  classroomsSharedCollection: IClassroom[] = [];
  sessionTypesSharedCollection: ISessionType[] = [];
  sessionModesSharedCollection: ISessionMode[] = [];
  partsSharedCollection: IPart[] = [];
  sessionJoinModesSharedCollection: ISessionJoinMode[] = [];
  groupsSharedCollection: IGroup[] = [];

  editForm: SessionFormGroup = this.sessionFormService.createSessionFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected sessionService: SessionService,
    protected sessionFormService: SessionFormService,
    protected professorService: ProfessorService,
    protected employeeService: EmployeeService,
    protected sessionLinkService: SessionLinkService,
    protected classroomService: ClassroomService,
    protected sessionTypeService: SessionTypeService,
    protected sessionModeService: SessionModeService,
    protected partService: PartService,
    protected sessionJoinModeService: SessionJoinModeService,
    protected groupService: GroupService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareProfessor = (o1: IProfessor | null, o2: IProfessor | null): boolean => this.professorService.compareProfessor(o1, o2);

  compareEmployee = (o1: IEmployee | null, o2: IEmployee | null): boolean => this.employeeService.compareEmployee(o1, o2);

  compareSessionLink = (o1: ISessionLink | null, o2: ISessionLink | null): boolean => this.sessionLinkService.compareSessionLink(o1, o2);

  compareClassroom = (o1: IClassroom | null, o2: IClassroom | null): boolean => this.classroomService.compareClassroom(o1, o2);

  compareSessionType = (o1: ISessionType | null, o2: ISessionType | null): boolean => this.sessionTypeService.compareSessionType(o1, o2);

  compareSessionMode = (o1: ISessionMode | null, o2: ISessionMode | null): boolean => this.sessionModeService.compareSessionMode(o1, o2);

  comparePart = (o1: IPart | null, o2: IPart | null): boolean => this.partService.comparePart(o1, o2);

  compareSessionJoinMode = (o1: ISessionJoinMode | null, o2: ISessionJoinMode | null): boolean =>
    this.sessionJoinModeService.compareSessionJoinMode(o1, o2);

  compareGroup = (o1: IGroup | null, o2: IGroup | null): boolean => this.groupService.compareGroup(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ session }) => {
      this.session = session;
      if (session) {
        this.updateForm(session);
      }

      this.loadRelationshipsOptions();
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

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const session = this.sessionFormService.getSession(this.editForm);
    if (session.id !== null) {
      this.subscribeToSaveResponse(this.sessionService.update(session));
    } else {
      this.subscribeToSaveResponse(this.sessionService.create(session));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISession>>): void {
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

  protected updateForm(session: ISession): void {
    this.session = session;
    this.sessionFormService.resetForm(this.editForm, session);

    this.professorsSharedCollection = this.professorService.addProfessorToCollectionIfMissing<IProfessor>(
      this.professorsSharedCollection,
      ...(session.professors ?? []),
    );
    this.employeesSharedCollection = this.employeeService.addEmployeeToCollectionIfMissing<IEmployee>(
      this.employeesSharedCollection,
      ...(session.employees ?? []),
    );
    this.sessionLinksSharedCollection = this.sessionLinkService.addSessionLinkToCollectionIfMissing<ISessionLink>(
      this.sessionLinksSharedCollection,
      ...(session.links ?? []),
    );
    this.classroomsSharedCollection = this.classroomService.addClassroomToCollectionIfMissing<IClassroom>(
      this.classroomsSharedCollection,
      session.classroom,
    );
    this.sessionTypesSharedCollection = this.sessionTypeService.addSessionTypeToCollectionIfMissing<ISessionType>(
      this.sessionTypesSharedCollection,
      session.type,
    );
    this.sessionModesSharedCollection = this.sessionModeService.addSessionModeToCollectionIfMissing<ISessionMode>(
      this.sessionModesSharedCollection,
      session.mode,
    );
    this.partsSharedCollection = this.partService.addPartToCollectionIfMissing<IPart>(this.partsSharedCollection, session.part);
    this.sessionJoinModesSharedCollection = this.sessionJoinModeService.addSessionJoinModeToCollectionIfMissing<ISessionJoinMode>(
      this.sessionJoinModesSharedCollection,
      session.jmode,
    );
    this.groupsSharedCollection = this.groupService.addGroupToCollectionIfMissing<IGroup>(this.groupsSharedCollection, session.group);
  }

  protected loadRelationshipsOptions(): void {
    this.professorService
      .query()
      .pipe(map((res: HttpResponse<IProfessor[]>) => res.body ?? []))
      .pipe(
        map((professors: IProfessor[]) =>
          this.professorService.addProfessorToCollectionIfMissing<IProfessor>(professors, ...(this.session?.professors ?? [])),
        ),
      )
      .subscribe((professors: IProfessor[]) => (this.professorsSharedCollection = professors));

    this.employeeService
      .query()
      .pipe(map((res: HttpResponse<IEmployee[]>) => res.body ?? []))
      .pipe(
        map((employees: IEmployee[]) =>
          this.employeeService.addEmployeeToCollectionIfMissing<IEmployee>(employees, ...(this.session?.employees ?? [])),
        ),
      )
      .subscribe((employees: IEmployee[]) => (this.employeesSharedCollection = employees));

    this.sessionLinkService
      .query()
      .pipe(map((res: HttpResponse<ISessionLink[]>) => res.body ?? []))
      .pipe(
        map((sessionLinks: ISessionLink[]) =>
          this.sessionLinkService.addSessionLinkToCollectionIfMissing<ISessionLink>(sessionLinks, ...(this.session?.links ?? [])),
        ),
      )
      .subscribe((sessionLinks: ISessionLink[]) => (this.sessionLinksSharedCollection = sessionLinks));

    this.classroomService
      .query()
      .pipe(map((res: HttpResponse<IClassroom[]>) => res.body ?? []))
      .pipe(
        map((classrooms: IClassroom[]) =>
          this.classroomService.addClassroomToCollectionIfMissing<IClassroom>(classrooms, this.session?.classroom),
        ),
      )
      .subscribe((classrooms: IClassroom[]) => (this.classroomsSharedCollection = classrooms));

    this.sessionTypeService
      .query()
      .pipe(map((res: HttpResponse<ISessionType[]>) => res.body ?? []))
      .pipe(
        map((sessionTypes: ISessionType[]) =>
          this.sessionTypeService.addSessionTypeToCollectionIfMissing<ISessionType>(sessionTypes, this.session?.type),
        ),
      )
      .subscribe((sessionTypes: ISessionType[]) => (this.sessionTypesSharedCollection = sessionTypes));

    this.sessionModeService
      .query()
      .pipe(map((res: HttpResponse<ISessionMode[]>) => res.body ?? []))
      .pipe(
        map((sessionModes: ISessionMode[]) =>
          this.sessionModeService.addSessionModeToCollectionIfMissing<ISessionMode>(sessionModes, this.session?.mode),
        ),
      )
      .subscribe((sessionModes: ISessionMode[]) => (this.sessionModesSharedCollection = sessionModes));

    this.partService
      .query()
      .pipe(map((res: HttpResponse<IPart[]>) => res.body ?? []))
      .pipe(map((parts: IPart[]) => this.partService.addPartToCollectionIfMissing<IPart>(parts, this.session?.part)))
      .subscribe((parts: IPart[]) => (this.partsSharedCollection = parts));

    this.sessionJoinModeService
      .query()
      .pipe(map((res: HttpResponse<ISessionJoinMode[]>) => res.body ?? []))
      .pipe(
        map((sessionJoinModes: ISessionJoinMode[]) =>
          this.sessionJoinModeService.addSessionJoinModeToCollectionIfMissing<ISessionJoinMode>(sessionJoinModes, this.session?.jmode),
        ),
      )
      .subscribe((sessionJoinModes: ISessionJoinMode[]) => (this.sessionJoinModesSharedCollection = sessionJoinModes));

    this.groupService
      .query()
      .pipe(map((res: HttpResponse<IGroup[]>) => res.body ?? []))
      .pipe(map((groups: IGroup[]) => this.groupService.addGroupToCollectionIfMissing<IGroup>(groups, this.session?.group)))
      .subscribe((groups: IGroup[]) => (this.groupsSharedCollection = groups));
  }
}
