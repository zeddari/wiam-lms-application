import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { ICotery } from 'app/entities/cotery/cotery.model';
import { CoteryService } from 'app/entities/cotery/service/cotery.service';
import { ICertificate } from '../certificate.model';
import { CertificateService } from '../service/certificate.service';
import { CertificateFormService } from './certificate-form.service';

import { CertificateUpdateComponent } from './certificate-update.component';

describe('Certificate Management Update Component', () => {
  let comp: CertificateUpdateComponent;
  let fixture: ComponentFixture<CertificateUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let certificateFormService: CertificateFormService;
  let certificateService: CertificateService;
  let studentService: StudentService;
  let coteryService: CoteryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), CertificateUpdateComponent],
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
      .overrideTemplate(CertificateUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CertificateUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    certificateFormService = TestBed.inject(CertificateFormService);
    certificateService = TestBed.inject(CertificateService);
    studentService = TestBed.inject(StudentService);
    coteryService = TestBed.inject(CoteryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Student query and add missing value', () => {
      const certificate: ICertificate = { id: 456 };
      const student: IStudent = { id: 16697 };
      certificate.student = student;

      const studentCollection: IStudent[] = [{ id: 18047 }];
      jest.spyOn(studentService, 'query').mockReturnValue(of(new HttpResponse({ body: studentCollection })));
      const additionalStudents = [student];
      const expectedCollection: IStudent[] = [...additionalStudents, ...studentCollection];
      jest.spyOn(studentService, 'addStudentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      expect(studentService.query).toHaveBeenCalled();
      expect(studentService.addStudentToCollectionIfMissing).toHaveBeenCalledWith(
        studentCollection,
        ...additionalStudents.map(expect.objectContaining),
      );
      expect(comp.studentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Cotery query and add missing value', () => {
      const certificate: ICertificate = { id: 456 };
      const cotery: ICotery = { id: 5253 };
      certificate.cotery = cotery;

      const coteryCollection: ICotery[] = [{ id: 5898 }];
      jest.spyOn(coteryService, 'query').mockReturnValue(of(new HttpResponse({ body: coteryCollection })));
      const additionalCoteries = [cotery];
      const expectedCollection: ICotery[] = [...additionalCoteries, ...coteryCollection];
      jest.spyOn(coteryService, 'addCoteryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      expect(coteryService.query).toHaveBeenCalled();
      expect(coteryService.addCoteryToCollectionIfMissing).toHaveBeenCalledWith(
        coteryCollection,
        ...additionalCoteries.map(expect.objectContaining),
      );
      expect(comp.coteriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const certificate: ICertificate = { id: 456 };
      const student: IStudent = { id: 745 };
      certificate.student = student;
      const cotery: ICotery = { id: 21042 };
      certificate.cotery = cotery;

      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      expect(comp.studentsSharedCollection).toContain(student);
      expect(comp.coteriesSharedCollection).toContain(cotery);
      expect(comp.certificate).toEqual(certificate);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICertificate>>();
      const certificate = { id: 123 };
      jest.spyOn(certificateFormService, 'getCertificate').mockReturnValue(certificate);
      jest.spyOn(certificateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: certificate }));
      saveSubject.complete();

      // THEN
      expect(certificateFormService.getCertificate).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(certificateService.update).toHaveBeenCalledWith(expect.objectContaining(certificate));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICertificate>>();
      const certificate = { id: 123 };
      jest.spyOn(certificateFormService, 'getCertificate').mockReturnValue({ id: null });
      jest.spyOn(certificateService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ certificate: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: certificate }));
      saveSubject.complete();

      // THEN
      expect(certificateFormService.getCertificate).toHaveBeenCalled();
      expect(certificateService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICertificate>>();
      const certificate = { id: 123 };
      jest.spyOn(certificateService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ certificate });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(certificateService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStudent', () => {
      it('Should forward to studentService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(studentService, 'compareStudent');
        comp.compareStudent(entity, entity2);
        expect(studentService.compareStudent).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCotery', () => {
      it('Should forward to coteryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(coteryService, 'compareCotery');
        comp.compareCotery(entity, entity2);
        expect(coteryService.compareCotery).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
