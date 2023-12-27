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
import { ICountry } from 'app/entities/country/country.model';
import { CountryService } from 'app/entities/country/service/country.service';
import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';
import { IExam } from 'app/entities/exam/exam.model';
import { ExamService } from 'app/entities/exam/service/exam.service';
import { Role } from 'app/entities/enumerations/role.model';
import { AccountStatus } from 'app/entities/enumerations/account-status.model';
import { Sex } from 'app/entities/enumerations/sex.model';
import { UserCustomService } from '../service/user-custom.service';
import { IUserCustom } from '../user-custom.model';
import { UserCustomFormService, UserCustomFormGroup } from './user-custom-form.service';

@Component({
  standalone: true,
  selector: 'jhi-user-custom-update',
  templateUrl: './user-custom-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class UserCustomUpdateComponent implements OnInit {
  isSaving = false;
  userCustom: IUserCustom | null = null;
  roleValues = Object.keys(Role);
  accountStatusValues = Object.keys(AccountStatus);
  sexValues = Object.keys(Sex);

  countriesSharedCollection: ICountry[] = [];
  jobsSharedCollection: IJob[] = [];
  examsSharedCollection: IExam[] = [];

  editForm: UserCustomFormGroup = this.userCustomFormService.createUserCustomFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected userCustomService: UserCustomService,
    protected userCustomFormService: UserCustomFormService,
    protected countryService: CountryService,
    protected jobService: JobService,
    protected examService: ExamService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareCountry = (o1: ICountry | null, o2: ICountry | null): boolean => this.countryService.compareCountry(o1, o2);

  compareJob = (o1: IJob | null, o2: IJob | null): boolean => this.jobService.compareJob(o1, o2);

  compareExam = (o1: IExam | null, o2: IExam | null): boolean => this.examService.compareExam(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userCustom }) => {
      this.userCustom = userCustom;
      if (userCustom) {
        this.updateForm(userCustom);
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
    const userCustom = this.userCustomFormService.getUserCustom(this.editForm);
    if (userCustom.id !== null) {
      this.subscribeToSaveResponse(this.userCustomService.update(userCustom));
    } else {
      this.subscribeToSaveResponse(this.userCustomService.create(userCustom));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserCustom>>): void {
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

  protected updateForm(userCustom: IUserCustom): void {
    this.userCustom = userCustom;
    this.userCustomFormService.resetForm(this.editForm, userCustom);

    this.countriesSharedCollection = this.countryService.addCountryToCollectionIfMissing<ICountry>(
      this.countriesSharedCollection,
      userCustom.country,
    );
    this.jobsSharedCollection = this.jobService.addJobToCollectionIfMissing<IJob>(this.jobsSharedCollection, userCustom.job);
    this.examsSharedCollection = this.examService.addExamToCollectionIfMissing<IExam>(
      this.examsSharedCollection,
      ...(userCustom.exams ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.countryService
      .query()
      .pipe(map((res: HttpResponse<ICountry[]>) => res.body ?? []))
      .pipe(
        map((countries: ICountry[]) => this.countryService.addCountryToCollectionIfMissing<ICountry>(countries, this.userCustom?.country)),
      )
      .subscribe((countries: ICountry[]) => (this.countriesSharedCollection = countries));

    this.jobService
      .query()
      .pipe(map((res: HttpResponse<IJob[]>) => res.body ?? []))
      .pipe(map((jobs: IJob[]) => this.jobService.addJobToCollectionIfMissing<IJob>(jobs, this.userCustom?.job)))
      .subscribe((jobs: IJob[]) => (this.jobsSharedCollection = jobs));

    this.examService
      .query()
      .pipe(map((res: HttpResponse<IExam[]>) => res.body ?? []))
      .pipe(map((exams: IExam[]) => this.examService.addExamToCollectionIfMissing<IExam>(exams, ...(this.userCustom?.exams ?? []))))
      .subscribe((exams: IExam[]) => (this.examsSharedCollection = exams));
  }
}
