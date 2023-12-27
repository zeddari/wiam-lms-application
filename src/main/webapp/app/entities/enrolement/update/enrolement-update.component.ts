import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { ICourse } from 'app/entities/course/course.model';
import { CourseService } from 'app/entities/course/service/course.service';
import { EnrolementService } from '../service/enrolement.service';
import { IEnrolement } from '../enrolement.model';
import { EnrolementFormService, EnrolementFormGroup } from './enrolement-form.service';

@Component({
  standalone: true,
  selector: 'jhi-enrolement-update',
  templateUrl: './enrolement-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class EnrolementUpdateComponent implements OnInit {
  isSaving = false;
  enrolement: IEnrolement | null = null;

  studentsSharedCollection: IStudent[] = [];
  coursesSharedCollection: ICourse[] = [];

  editForm: EnrolementFormGroup = this.enrolementFormService.createEnrolementFormGroup();

  constructor(
    protected enrolementService: EnrolementService,
    protected enrolementFormService: EnrolementFormService,
    protected studentService: StudentService,
    protected courseService: CourseService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareStudent = (o1: IStudent | null, o2: IStudent | null): boolean => this.studentService.compareStudent(o1, o2);

  compareCourse = (o1: ICourse | null, o2: ICourse | null): boolean => this.courseService.compareCourse(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ enrolement }) => {
      this.enrolement = enrolement;
      if (enrolement) {
        this.updateForm(enrolement);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const enrolement = this.enrolementFormService.getEnrolement(this.editForm);
    if (enrolement.id !== null) {
      this.subscribeToSaveResponse(this.enrolementService.update(enrolement));
    } else {
      this.subscribeToSaveResponse(this.enrolementService.create(enrolement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEnrolement>>): void {
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

  protected updateForm(enrolement: IEnrolement): void {
    this.enrolement = enrolement;
    this.enrolementFormService.resetForm(this.editForm, enrolement);

    this.studentsSharedCollection = this.studentService.addStudentToCollectionIfMissing<IStudent>(
      this.studentsSharedCollection,
      enrolement.student,
    );
    this.coursesSharedCollection = this.courseService.addCourseToCollectionIfMissing<ICourse>(
      this.coursesSharedCollection,
      enrolement.course,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.studentService
      .query()
      .pipe(map((res: HttpResponse<IStudent[]>) => res.body ?? []))
      .pipe(
        map((students: IStudent[]) => this.studentService.addStudentToCollectionIfMissing<IStudent>(students, this.enrolement?.student)),
      )
      .subscribe((students: IStudent[]) => (this.studentsSharedCollection = students));

    this.courseService
      .query()
      .pipe(map((res: HttpResponse<ICourse[]>) => res.body ?? []))
      .pipe(map((courses: ICourse[]) => this.courseService.addCourseToCollectionIfMissing<ICourse>(courses, this.enrolement?.course)))
      .subscribe((courses: ICourse[]) => (this.coursesSharedCollection = courses));
  }
}
