import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ICotery } from 'app/entities/cotery/cotery.model';
import { CoteryService } from 'app/entities/cotery/service/cotery.service';
import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { ICoteryHistory } from '../cotery-history.model';
import { CoteryHistoryService } from '../service/cotery-history.service';
import { CoteryHistoryFormService } from './cotery-history-form.service';

import { CoteryHistoryUpdateComponent } from './cotery-history-update.component';

describe('CoteryHistory Management Update Component', () => {
  let comp: CoteryHistoryUpdateComponent;
  let fixture: ComponentFixture<CoteryHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let coteryHistoryFormService: CoteryHistoryFormService;
  let coteryHistoryService: CoteryHistoryService;
  let coteryService: CoteryService;
  let studentService: StudentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), CoteryHistoryUpdateComponent],
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
      .overrideTemplate(CoteryHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CoteryHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    coteryHistoryFormService = TestBed.inject(CoteryHistoryFormService);
    coteryHistoryService = TestBed.inject(CoteryHistoryService);
    coteryService = TestBed.inject(CoteryService);
    studentService = TestBed.inject(StudentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Cotery query and add missing value', () => {
      const coteryHistory: ICoteryHistory = { id: 456 };
      const student2: ICotery = { id: 19386 };
      coteryHistory.student2 = student2;

      const coteryCollection: ICotery[] = [{ id: 28587 }];
      jest.spyOn(coteryService, 'query').mockReturnValue(of(new HttpResponse({ body: coteryCollection })));
      const additionalCoteries = [student2];
      const expectedCollection: ICotery[] = [...additionalCoteries, ...coteryCollection];
      jest.spyOn(coteryService, 'addCoteryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ coteryHistory });
      comp.ngOnInit();

      expect(coteryService.query).toHaveBeenCalled();
      expect(coteryService.addCoteryToCollectionIfMissing).toHaveBeenCalledWith(
        coteryCollection,
        ...additionalCoteries.map(expect.objectContaining),
      );
      expect(comp.coteriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Student query and add missing value', () => {
      const coteryHistory: ICoteryHistory = { id: 456 };
      const student: IStudent = { id: 3843 };
      coteryHistory.student = student;

      const studentCollection: IStudent[] = [{ id: 22597 }];
      jest.spyOn(studentService, 'query').mockReturnValue(of(new HttpResponse({ body: studentCollection })));
      const additionalStudents = [student];
      const expectedCollection: IStudent[] = [...additionalStudents, ...studentCollection];
      jest.spyOn(studentService, 'addStudentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ coteryHistory });
      comp.ngOnInit();

      expect(studentService.query).toHaveBeenCalled();
      expect(studentService.addStudentToCollectionIfMissing).toHaveBeenCalledWith(
        studentCollection,
        ...additionalStudents.map(expect.objectContaining),
      );
      expect(comp.studentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const coteryHistory: ICoteryHistory = { id: 456 };
      const student2: ICotery = { id: 25453 };
      coteryHistory.student2 = student2;
      const student: IStudent = { id: 2412 };
      coteryHistory.student = student;

      activatedRoute.data = of({ coteryHistory });
      comp.ngOnInit();

      expect(comp.coteriesSharedCollection).toContain(student2);
      expect(comp.studentsSharedCollection).toContain(student);
      expect(comp.coteryHistory).toEqual(coteryHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICoteryHistory>>();
      const coteryHistory = { id: 123 };
      jest.spyOn(coteryHistoryFormService, 'getCoteryHistory').mockReturnValue(coteryHistory);
      jest.spyOn(coteryHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coteryHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: coteryHistory }));
      saveSubject.complete();

      // THEN
      expect(coteryHistoryFormService.getCoteryHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(coteryHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(coteryHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICoteryHistory>>();
      const coteryHistory = { id: 123 };
      jest.spyOn(coteryHistoryFormService, 'getCoteryHistory').mockReturnValue({ id: null });
      jest.spyOn(coteryHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coteryHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: coteryHistory }));
      saveSubject.complete();

      // THEN
      expect(coteryHistoryFormService.getCoteryHistory).toHaveBeenCalled();
      expect(coteryHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICoteryHistory>>();
      const coteryHistory = { id: 123 };
      jest.spyOn(coteryHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ coteryHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(coteryHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCotery', () => {
      it('Should forward to coteryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(coteryService, 'compareCotery');
        comp.compareCotery(entity, entity2);
        expect(coteryService.compareCotery).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareStudent', () => {
      it('Should forward to studentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(studentService, 'compareStudent');
        comp.compareStudent(entity, entity2);
        expect(studentService.compareStudent).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
