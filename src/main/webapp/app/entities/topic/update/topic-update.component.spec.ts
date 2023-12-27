import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TopicService } from '../service/topic.service';
import { ITopic } from '../topic.model';
import { TopicFormService } from './topic-form.service';

import { TopicUpdateComponent } from './topic-update.component';

describe('Topic Management Update Component', () => {
  let comp: TopicUpdateComponent;
  let fixture: ComponentFixture<TopicUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let topicFormService: TopicFormService;
  let topicService: TopicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TopicUpdateComponent],
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
      .overrideTemplate(TopicUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TopicUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    topicFormService = TestBed.inject(TopicFormService);
    topicService = TestBed.inject(TopicService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Topic query and add missing value', () => {
      const topic: ITopic = { id: 456 };
      const topic2: ITopic = { id: 26971 };
      topic.topic2 = topic2;

      const topicCollection: ITopic[] = [{ id: 19554 }];
      jest.spyOn(topicService, 'query').mockReturnValue(of(new HttpResponse({ body: topicCollection })));
      const additionalTopics = [topic2];
      const expectedCollection: ITopic[] = [...additionalTopics, ...topicCollection];
      jest.spyOn(topicService, 'addTopicToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ topic });
      comp.ngOnInit();

      expect(topicService.query).toHaveBeenCalled();
      expect(topicService.addTopicToCollectionIfMissing).toHaveBeenCalledWith(
        topicCollection,
        ...additionalTopics.map(expect.objectContaining),
      );
      expect(comp.topicsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const topic: ITopic = { id: 456 };
      const topic2: ITopic = { id: 8837 };
      topic.topic2 = topic2;

      activatedRoute.data = of({ topic });
      comp.ngOnInit();

      expect(comp.topicsSharedCollection).toContain(topic2);
      expect(comp.topic).toEqual(topic);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITopic>>();
      const topic = { id: 123 };
      jest.spyOn(topicFormService, 'getTopic').mockReturnValue(topic);
      jest.spyOn(topicService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ topic });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: topic }));
      saveSubject.complete();

      // THEN
      expect(topicFormService.getTopic).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(topicService.update).toHaveBeenCalledWith(expect.objectContaining(topic));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITopic>>();
      const topic = { id: 123 };
      jest.spyOn(topicFormService, 'getTopic').mockReturnValue({ id: null });
      jest.spyOn(topicService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ topic: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: topic }));
      saveSubject.complete();

      // THEN
      expect(topicFormService.getTopic).toHaveBeenCalled();
      expect(topicService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITopic>>();
      const topic = { id: 123 };
      jest.spyOn(topicService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ topic });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(topicService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTopic', () => {
      it('Should forward to topicService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(topicService, 'compareTopic');
        comp.compareTopic(entity, entity2);
        expect(topicService.compareTopic).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
