import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IGroup } from '../group.model';
import { GroupService } from '../service/group.service';
import { GroupFormService, GroupFormGroup } from './group-form.service';

@Component({
  standalone: true,
  selector: 'jhi-group-update',
  templateUrl: './group-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class GroupUpdateComponent implements OnInit {
  isSaving = false;
  group: IGroup | null = null;

  groupsSharedCollection: IGroup[] = [];

  editForm: GroupFormGroup = this.groupFormService.createGroupFormGroup();

  constructor(
    protected groupService: GroupService,
    protected groupFormService: GroupFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareGroup = (o1: IGroup | null, o2: IGroup | null): boolean => this.groupService.compareGroup(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ group }) => {
      this.group = group;
      if (group) {
        this.updateForm(group);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const group = this.groupFormService.getGroup(this.editForm);
    if (group.id !== null) {
      this.subscribeToSaveResponse(this.groupService.update(group));
    } else {
      this.subscribeToSaveResponse(this.groupService.create(group));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGroup>>): void {
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

  protected updateForm(group: IGroup): void {
    this.group = group;
    this.groupFormService.resetForm(this.editForm, group);

    this.groupsSharedCollection = this.groupService.addGroupToCollectionIfMissing<IGroup>(this.groupsSharedCollection, group.group1);
  }

  protected loadRelationshipsOptions(): void {
    this.groupService
      .query()
      .pipe(map((res: HttpResponse<IGroup[]>) => res.body ?? []))
      .pipe(map((groups: IGroup[]) => this.groupService.addGroupToCollectionIfMissing<IGroup>(groups, this.group?.group1)))
      .subscribe((groups: IGroup[]) => (this.groupsSharedCollection = groups));
  }
}
