import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ILevel } from '../level.model';
import { LevelService } from '../service/level.service';
import { LevelFormService, LevelFormGroup } from './level-form.service';

@Component({
  standalone: true,
  selector: 'jhi-level-update',
  templateUrl: './level-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class LevelUpdateComponent implements OnInit {
  isSaving = false;
  level: ILevel | null = null;

  editForm: LevelFormGroup = this.levelFormService.createLevelFormGroup();

  constructor(
    protected levelService: LevelService,
    protected levelFormService: LevelFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ level }) => {
      this.level = level;
      if (level) {
        this.updateForm(level);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const level = this.levelFormService.getLevel(this.editForm);
    if (level.id !== null) {
      this.subscribeToSaveResponse(this.levelService.update(level));
    } else {
      this.subscribeToSaveResponse(this.levelService.create(level));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILevel>>): void {
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

  protected updateForm(level: ILevel): void {
    this.level = level;
    this.levelFormService.resetForm(this.editForm, level);
  }
}
