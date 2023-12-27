import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { UserCustomService } from 'app/entities/user-custom/service/user-custom.service';
import { IStudent } from 'app/entities/student/student.model';
import { StudentService } from 'app/entities/student/service/student.service';
import { ISponsor } from '../sponsor.model';
import { SponsorService } from '../service/sponsor.service';
import { SponsorFormService } from './sponsor-form.service';

import { SponsorUpdateComponent } from './sponsor-update.component';

describe('Sponsor Management Update Component', () => {
  let comp: SponsorUpdateComponent;
  let fixture: ComponentFixture<SponsorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sponsorFormService: SponsorFormService;
  let sponsorService: SponsorService;
  let userCustomService: UserCustomService;
  let studentService: StudentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SponsorUpdateComponent],
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
      .overrideTemplate(SponsorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SponsorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sponsorFormService = TestBed.inject(SponsorFormService);
    sponsorService = TestBed.inject(SponsorService);
    userCustomService = TestBed.inject(UserCustomService);
    studentService = TestBed.inject(StudentService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call userCustom query and add missing value', () => {
      const sponsor: ISponsor = { id: 456 };
      const userCustom: IUserCustom = { id: 8853 };
      sponsor.userCustom = userCustom;

      const userCustomCollection: IUserCustom[] = [{ id: 49 }];
      jest.spyOn(userCustomService, 'query').mockReturnValue(of(new HttpResponse({ body: userCustomCollection })));
      const expectedCollection: IUserCustom[] = [userCustom, ...userCustomCollection];
      jest.spyOn(userCustomService, 'addUserCustomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sponsor });
      comp.ngOnInit();

      expect(userCustomService.query).toHaveBeenCalled();
      expect(userCustomService.addUserCustomToCollectionIfMissing).toHaveBeenCalledWith(userCustomCollection, userCustom);
      expect(comp.userCustomsCollection).toEqual(expectedCollection);
    });

    it('Should call Student query and add missing value', () => {
      const sponsor: ISponsor = { id: 456 };
      const students: IStudent[] = [{ id: 29561 }];
      sponsor.students = students;

      const studentCollection: IStudent[] = [{ id: 7626 }];
      jest.spyOn(studentService, 'query').mockReturnValue(of(new HttpResponse({ body: studentCollection })));
      const additionalStudents = [...students];
      const expectedCollection: IStudent[] = [...additionalStudents, ...studentCollection];
      jest.spyOn(studentService, 'addStudentToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sponsor });
      comp.ngOnInit();

      expect(studentService.query).toHaveBeenCalled();
      expect(studentService.addStudentToCollectionIfMissing).toHaveBeenCalledWith(
        studentCollection,
        ...additionalStudents.map(expect.objectContaining),
      );
      expect(comp.studentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const sponsor: ISponsor = { id: 456 };
      const userCustom: IUserCustom = { id: 17166 };
      sponsor.userCustom = userCustom;
      const students: IStudent = { id: 18383 };
      sponsor.students = [students];

      activatedRoute.data = of({ sponsor });
      comp.ngOnInit();

      expect(comp.userCustomsCollection).toContain(userCustom);
      expect(comp.studentsSharedCollection).toContain(students);
      expect(comp.sponsor).toEqual(sponsor);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISponsor>>();
      const sponsor = { id: 123 };
      jest.spyOn(sponsorFormService, 'getSponsor').mockReturnValue(sponsor);
      jest.spyOn(sponsorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sponsor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sponsor }));
      saveSubject.complete();

      // THEN
      expect(sponsorFormService.getSponsor).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sponsorService.update).toHaveBeenCalledWith(expect.objectContaining(sponsor));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISponsor>>();
      const sponsor = { id: 123 };
      jest.spyOn(sponsorFormService, 'getSponsor').mockReturnValue({ id: null });
      jest.spyOn(sponsorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sponsor: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sponsor }));
      saveSubject.complete();

      // THEN
      expect(sponsorFormService.getSponsor).toHaveBeenCalled();
      expect(sponsorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISponsor>>();
      const sponsor = { id: 123 };
      jest.spyOn(sponsorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sponsor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sponsorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUserCustom', () => {
      it('Should forward to userCustomService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userCustomService, 'compareUserCustom');
        comp.compareUserCustom(entity, entity2);
        expect(userCustomService.compareUserCustom).toHaveBeenCalledWith(entity, entity2);
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
