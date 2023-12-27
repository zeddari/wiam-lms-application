import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { ICotery } from 'app/entities/cotery/cotery.model';
import { CoteryService } from 'app/entities/cotery/service/cotery.service';
import { CertificateType } from 'app/entities/enumerations/certificate-type.model';
import { CertificateService } from '../service/certificate.service';
import { ICertificate } from '../certificate.model';
import { CertificateFormService, CertificateFormGroup } from './certificate-form.service';

@Component({
  standalone: true,
  selector: 'jhi-certificate-update',
  templateUrl: './certificate-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CertificateUpdateComponent implements OnInit {
  isSaving = false;
  certificate: ICertificate | null = null;
  certificateTypeValues = Object.keys(CertificateType);

  studentsSharedCollection: IStudent[] = [];
  coteriesSharedCollection: ICotery[] = [];

  editForm: CertificateFormGroup = this.certificateFormService.createCertificateFormGroup();

  constructor(
    protected certificateService: CertificateService,
    protected certificateFormService: CertificateFormService,
    protected studentService: StudentService,
    protected coteryService: CoteryService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareStudent = (o1: IStudent | null, o2: IStudent | null): boolean => this.studentService.compareStudent(o1, o2);

  compareCotery = (o1: ICotery | null, o2: ICotery | null): boolean => this.coteryService.compareCotery(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ certificate }) => {
      this.certificate = certificate;
      if (certificate) {
        this.updateForm(certificate);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const certificate = this.certificateFormService.getCertificate(this.editForm);
    if (certificate.id !== null) {
      this.subscribeToSaveResponse(this.certificateService.update(certificate));
    } else {
      this.subscribeToSaveResponse(this.certificateService.create(certificate));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICertificate>>): void {
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

  protected updateForm(certificate: ICertificate): void {
    this.certificate = certificate;
    this.certificateFormService.resetForm(this.editForm, certificate);

    this.studentsSharedCollection = this.studentService.addStudentToCollectionIfMissing<IStudent>(
      this.studentsSharedCollection,
      certificate.student,
    );
    this.coteriesSharedCollection = this.coteryService.addCoteryToCollectionIfMissing<ICotery>(
      this.coteriesSharedCollection,
      certificate.cotery,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.studentService
      .query()
      .pipe(map((res: HttpResponse<IStudent[]>) => res.body ?? []))
      .pipe(
        map((students: IStudent[]) => this.studentService.addStudentToCollectionIfMissing<IStudent>(students, this.certificate?.student)),
      )
      .subscribe((students: IStudent[]) => (this.studentsSharedCollection = students));

    this.coteryService
      .query()
      .pipe(map((res: HttpResponse<ICotery[]>) => res.body ?? []))
      .pipe(map((coteries: ICotery[]) => this.coteryService.addCoteryToCollectionIfMissing<ICotery>(coteries, this.certificate?.cotery)))
      .subscribe((coteries: ICotery[]) => (this.coteriesSharedCollection = coteries));
  }
}
