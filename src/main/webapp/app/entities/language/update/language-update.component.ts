import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { UserCustomService } from 'app/entities/user-custom/service/user-custom.service';
import { ILanguage } from '../language.model';
import { LanguageService } from '../service/language.service';
import { LanguageFormService, LanguageFormGroup } from './language-form.service';

@Component({
  standalone: true,
  selector: 'jhi-language-update',
  templateUrl: './language-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LanguageUpdateComponent implements OnInit {
  isSaving = false;
  language: ILanguage | null = null;

  userCustomsSharedCollection: IUserCustom[] = [];

  editForm: LanguageFormGroup = this.languageFormService.createLanguageFormGroup();

  constructor(
    protected languageService: LanguageService,
    protected languageFormService: LanguageFormService,
    protected userCustomService: UserCustomService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareUserCustom = (o1: IUserCustom | null, o2: IUserCustom | null): boolean => this.userCustomService.compareUserCustom(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ language }) => {
      this.language = language;
      if (language) {
        this.updateForm(language);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const language = this.languageFormService.getLanguage(this.editForm);
    if (language.id !== null) {
      this.subscribeToSaveResponse(this.languageService.update(language));
    } else {
      this.subscribeToSaveResponse(this.languageService.create(language));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILanguage>>): void {
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

  protected updateForm(language: ILanguage): void {
    this.language = language;
    this.languageFormService.resetForm(this.editForm, language);

    this.userCustomsSharedCollection = this.userCustomService.addUserCustomToCollectionIfMissing<IUserCustom>(
      this.userCustomsSharedCollection,
      language.userCustom,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userCustomService
      .query()
      .pipe(map((res: HttpResponse<IUserCustom[]>) => res.body ?? []))
      .pipe(
        map((userCustoms: IUserCustom[]) =>
          this.userCustomService.addUserCustomToCollectionIfMissing<IUserCustom>(userCustoms, this.language?.userCustom),
        ),
      )
      .subscribe((userCustoms: IUserCustom[]) => (this.userCustomsSharedCollection = userCustoms));
  }
}
