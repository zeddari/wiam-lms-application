import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ICourse } from 'app/entities/course/course.model';
import { CourseService } from 'app/entities/course/service/course.service';
import { PartService } from '../service/part.service';
import { IPart } from '../part.model';
import { PartFormService } from './part-form.service';

import { PartUpdateComponent } from './part-update.component';

describe('Part Management Update Component', () => {
  let comp: PartUpdateComponent;
  let fixture: ComponentFixture<PartUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let partFormService: PartFormService;
  let partService: PartService;
  let courseService: CourseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), PartUpdateComponent],
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
      .overrideTemplate(PartUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PartUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    partFormService = TestBed.inject(PartFormService);
    partService = TestBed.inject(PartService);
    courseService = TestBed.inject(CourseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Part query and add missing value', () => {
      const part: IPart = { id: 456 };
      const part1: IPart = { id: 2441 };
      part.part1 = part1;

      const partCollection: IPart[] = [{ id: 26186 }];
      jest.spyOn(partService, 'query').mockReturnValue(of(new HttpResponse({ body: partCollection })));
      const additionalParts = [part1];
      const expectedCollection: IPart[] = [...additionalParts, ...partCollection];
      jest.spyOn(partService, 'addPartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ part });
      comp.ngOnInit();

      expect(partService.query).toHaveBeenCalled();
      expect(partService.addPartToCollectionIfMissing).toHaveBeenCalledWith(
        partCollection,
        ...additionalParts.map(expect.objectContaining),
      );
      expect(comp.partsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Course query and add missing value', () => {
      const part: IPart = { id: 456 };
      const course: ICourse = { id: 8466 };
      part.course = course;

      const courseCollection: ICourse[] = [{ id: 31564 }];
      jest.spyOn(courseService, 'query').mockReturnValue(of(new HttpResponse({ body: courseCollection })));
      const additionalCourses = [course];
      const expectedCollection: ICourse[] = [...additionalCourses, ...courseCollection];
      jest.spyOn(courseService, 'addCourseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ part });
      comp.ngOnInit();

      expect(courseService.query).toHaveBeenCalled();
      expect(courseService.addCourseToCollectionIfMissing).toHaveBeenCalledWith(
        courseCollection,
        ...additionalCourses.map(expect.objectContaining),
      );
      expect(comp.coursesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const part: IPart = { id: 456 };
      const part1: IPart = { id: 3941 };
      part.part1 = part1;
      const course: ICourse = { id: 12019 };
      part.course = course;

      activatedRoute.data = of({ part });
      comp.ngOnInit();

      expect(comp.partsSharedCollection).toContain(part1);
      expect(comp.coursesSharedCollection).toContain(course);
      expect(comp.part).toEqual(part);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPart>>();
      const part = { id: 123 };
      jest.spyOn(partFormService, 'getPart').mockReturnValue(part);
      jest.spyOn(partService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ part });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: part }));
      saveSubject.complete();

      // THEN
      expect(partFormService.getPart).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(partService.update).toHaveBeenCalledWith(expect.objectContaining(part));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPart>>();
      const part = { id: 123 };
      jest.spyOn(partFormService, 'getPart').mockReturnValue({ id: null });
      jest.spyOn(partService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ part: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: part }));
      saveSubject.complete();

      // THEN
      expect(partFormService.getPart).toHaveBeenCalled();
      expect(partService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPart>>();
      const part = { id: 123 };
      jest.spyOn(partService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ part });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(partService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePart', () => {
      it('Should forward to partService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(partService, 'comparePart');
        comp.comparePart(entity, entity2);
        expect(partService.comparePart).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCourse', () => {
      it('Should forward to courseService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(courseService, 'compareCourse');
        comp.compareCourse(entity, entity2);
        expect(courseService.compareCourse).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
