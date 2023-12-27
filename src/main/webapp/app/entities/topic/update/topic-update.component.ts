import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ITopic } from '../topic.model';
import { TopicService } from '../service/topic.service';
import { TopicFormService, TopicFormGroup } from './topic-form.service';

@Component({
  standalone: true,
  selector: 'jhi-topic-update',
  templateUrl: './topic-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class TopicUpdateComponent implements OnInit {
  isSaving = false;
  topic: ITopic | null = null;

  topicsSharedCollection: ITopic[] = [];

  editForm: TopicFormGroup = this.topicFormService.createTopicFormGroup();

  constructor(
    protected topicService: TopicService,
    protected topicFormService: TopicFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  compareTopic = (o1: ITopic | null, o2: ITopic | null): boolean => this.topicService.compareTopic(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ topic }) => {
      this.topic = topic;
      if (topic) {
        this.updateForm(topic);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const topic = this.topicFormService.getTopic(this.editForm);
    if (topic.id !== null) {
      this.subscribeToSaveResponse(this.topicService.update(topic));
    } else {
      this.subscribeToSaveResponse(this.topicService.create(topic));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITopic>>): void {
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

  protected updateForm(topic: ITopic): void {
    this.topic = topic;
    this.topicFormService.resetForm(this.editForm, topic);

    this.topicsSharedCollection = this.topicService.addTopicToCollectionIfMissing<ITopic>(this.topicsSharedCollection, topic.topic2);
  }

  protected loadRelationshipsOptions(): void {
    this.topicService
      .query()
      .pipe(map((res: HttpResponse<ITopic[]>) => res.body ?? []))
      .pipe(map((topics: ITopic[]) => this.topicService.addTopicToCollectionIfMissing<ITopic>(topics, this.topic?.topic2)))
      .subscribe((topics: ITopic[]) => (this.topicsSharedCollection = topics));
  }
}
