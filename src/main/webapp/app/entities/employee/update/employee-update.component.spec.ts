import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { UserCustomService } from 'app/entities/user-custom/service/user-custom.service';
import { IDepartement } from 'app/entities/departement/departement.model';
import { DepartementService } from 'app/entities/departement/service/departement.service';
import { IJobTitle } from 'app/entities/job-title/job-title.model';
import { JobTitleService } from 'app/entities/job-title/service/job-title.service';
import { IEmployee } from '../employee.model';
import { EmployeeService } from '../service/employee.service';
import { EmployeeFormService } from './employee-form.service';

import { EmployeeUpdateComponent } from './employee-update.component';

describe('Employee Management Update Component', () => {
  let comp: EmployeeUpdateComponent;
  let fixture: ComponentFixture<EmployeeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let employeeFormService: EmployeeFormService;
  let employeeService: EmployeeService;
  let userCustomService: UserCustomService;
  let departementService: DepartementService;
  let jobTitleService: JobTitleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), EmployeeUpdateComponent],
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
      .overrideTemplate(EmployeeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EmployeeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    employeeFormService = TestBed.inject(EmployeeFormService);
    employeeService = TestBed.inject(EmployeeService);
    userCustomService = TestBed.inject(UserCustomService);
    departementService = TestBed.inject(DepartementService);
    jobTitleService = TestBed.inject(JobTitleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call userCustom query and add missing value', () => {
      const employee: IEmployee = { id: 456 };
      const userCustom: IUserCustom = { id: 9663 };
      employee.userCustom = userCustom;

      const userCustomCollection: IUserCustom[] = [{ id: 31818 }];
      jest.spyOn(userCustomService, 'query').mockReturnValue(of(new HttpResponse({ body: userCustomCollection })));
      const expectedCollection: IUserCustom[] = [userCustom, ...userCustomCollection];
      jest.spyOn(userCustomService, 'addUserCustomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ employee });
      comp.ngOnInit();

      expect(userCustomService.query).toHaveBeenCalled();
      expect(userCustomService.addUserCustomToCollectionIfMissing).toHaveBeenCalledWith(userCustomCollection, userCustom);
      expect(comp.userCustomsCollection).toEqual(expectedCollection);
    });

    it('Should call Departement query and add missing value', () => {
      const employee: IEmployee = { id: 456 };
      const departement: IDepartement = { id: 26361 };
      employee.departement = departement;

      const departementCollection: IDepartement[] = [{ id: 10255 }];
      jest.spyOn(departementService, 'query').mockReturnValue(of(new HttpResponse({ body: departementCollection })));
      const additionalDepartements = [departement];
      const expectedCollection: IDepartement[] = [...additionalDepartements, ...departementCollection];
      jest.spyOn(departementService, 'addDepartementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ employee });
      comp.ngOnInit();

      expect(departementService.query).toHaveBeenCalled();
      expect(departementService.addDepartementToCollectionIfMissing).toHaveBeenCalledWith(
        departementCollection,
        ...additionalDepartements.map(expect.objectContaining),
      );
      expect(comp.departementsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call JobTitle query and add missing value', () => {
      const employee: IEmployee = { id: 456 };
      const job: IJobTitle = { id: 15424 };
      employee.job = job;

      const jobTitleCollection: IJobTitle[] = [{ id: 24391 }];
      jest.spyOn(jobTitleService, 'query').mockReturnValue(of(new HttpResponse({ body: jobTitleCollection })));
      const additionalJobTitles = [job];
      const expectedCollection: IJobTitle[] = [...additionalJobTitles, ...jobTitleCollection];
      jest.spyOn(jobTitleService, 'addJobTitleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ employee });
      comp.ngOnInit();

      expect(jobTitleService.query).toHaveBeenCalled();
      expect(jobTitleService.addJobTitleToCollectionIfMissing).toHaveBeenCalledWith(
        jobTitleCollection,
        ...additionalJobTitles.map(expect.objectContaining),
      );
      expect(comp.jobTitlesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const employee: IEmployee = { id: 456 };
      const userCustom: IUserCustom = { id: 13690 };
      employee.userCustom = userCustom;
      const departement: IDepartement = { id: 15792 };
      employee.departement = departement;
      const job: IJobTitle = { id: 22492 };
      employee.job = job;

      activatedRoute.data = of({ employee });
      comp.ngOnInit();

      expect(comp.userCustomsCollection).toContain(userCustom);
      expect(comp.departementsSharedCollection).toContain(departement);
      expect(comp.jobTitlesSharedCollection).toContain(job);
      expect(comp.employee).toEqual(employee);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmployee>>();
      const employee = { id: 123 };
      jest.spyOn(employeeFormService, 'getEmployee').mockReturnValue(employee);
      jest.spyOn(employeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ employee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: employee }));
      saveSubject.complete();

      // THEN
      expect(employeeFormService.getEmployee).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(employeeService.update).toHaveBeenCalledWith(expect.objectContaining(employee));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmployee>>();
      const employee = { id: 123 };
      jest.spyOn(employeeFormService, 'getEmployee').mockReturnValue({ id: null });
      jest.spyOn(employeeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ employee: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: employee }));
      saveSubject.complete();

      // THEN
      expect(employeeFormService.getEmployee).toHaveBeenCalled();
      expect(employeeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IEmployee>>();
      const employee = { id: 123 };
      jest.spyOn(employeeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ employee });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(employeeService.update).toHaveBeenCalled();
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

    describe('compareDepartement', () => {
      it('Should forward to departementService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(departementService, 'compareDepartement');
        comp.compareDepartement(entity, entity2);
        expect(departementService.compareDepartement).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareJobTitle', () => {
      it('Should forward to jobTitleService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(jobTitleService, 'compareJobTitle');
        comp.compareJobTitle(entity, entity2);
        expect(jobTitleService.compareJobTitle).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
