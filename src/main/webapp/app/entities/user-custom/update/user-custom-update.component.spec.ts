import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ICountry } from 'app/entities/country/country.model';
import { CountryService } from 'app/entities/country/service/country.service';
import { IJob } from 'app/entities/job/job.model';
import { JobService } from 'app/entities/job/service/job.service';
import { IExam } from 'app/entities/exam/exam.model';
import { ExamService } from 'app/entities/exam/service/exam.service';
import { IUserCustom } from '../user-custom.model';
import { UserCustomService } from '../service/user-custom.service';
import { UserCustomFormService } from './user-custom-form.service';

import { UserCustomUpdateComponent } from './user-custom-update.component';

describe('UserCustom Management Update Component', () => {
  let comp: UserCustomUpdateComponent;
  let fixture: ComponentFixture<UserCustomUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let userCustomFormService: UserCustomFormService;
  let userCustomService: UserCustomService;
  let countryService: CountryService;
  let jobService: JobService;
  let examService: ExamService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), UserCustomUpdateComponent],
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
      .overrideTemplate(UserCustomUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(UserCustomUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    userCustomFormService = TestBed.inject(UserCustomFormService);
    userCustomService = TestBed.inject(UserCustomService);
    countryService = TestBed.inject(CountryService);
    jobService = TestBed.inject(JobService);
    examService = TestBed.inject(ExamService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Country query and add missing value', () => {
      const userCustom: IUserCustom = { id: 456 };
      const country: ICountry = { id: 15515 };
      userCustom.country = country;

      const countryCollection: ICountry[] = [{ id: 15174 }];
      jest.spyOn(countryService, 'query').mockReturnValue(of(new HttpResponse({ body: countryCollection })));
      const additionalCountries = [country];
      const expectedCollection: ICountry[] = [...additionalCountries, ...countryCollection];
      jest.spyOn(countryService, 'addCountryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userCustom });
      comp.ngOnInit();

      expect(countryService.query).toHaveBeenCalled();
      expect(countryService.addCountryToCollectionIfMissing).toHaveBeenCalledWith(
        countryCollection,
        ...additionalCountries.map(expect.objectContaining),
      );
      expect(comp.countriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Job query and add missing value', () => {
      const userCustom: IUserCustom = { id: 456 };
      const job: IJob = { id: 15796 };
      userCustom.job = job;

      const jobCollection: IJob[] = [{ id: 8428 }];
      jest.spyOn(jobService, 'query').mockReturnValue(of(new HttpResponse({ body: jobCollection })));
      const additionalJobs = [job];
      const expectedCollection: IJob[] = [...additionalJobs, ...jobCollection];
      jest.spyOn(jobService, 'addJobToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userCustom });
      comp.ngOnInit();

      expect(jobService.query).toHaveBeenCalled();
      expect(jobService.addJobToCollectionIfMissing).toHaveBeenCalledWith(jobCollection, ...additionalJobs.map(expect.objectContaining));
      expect(comp.jobsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Exam query and add missing value', () => {
      const userCustom: IUserCustom = { id: 456 };
      const exams: IExam[] = [{ id: 2575 }];
      userCustom.exams = exams;

      const examCollection: IExam[] = [{ id: 2647 }];
      jest.spyOn(examService, 'query').mockReturnValue(of(new HttpResponse({ body: examCollection })));
      const additionalExams = [...exams];
      const expectedCollection: IExam[] = [...additionalExams, ...examCollection];
      jest.spyOn(examService, 'addExamToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ userCustom });
      comp.ngOnInit();

      expect(examService.query).toHaveBeenCalled();
      expect(examService.addExamToCollectionIfMissing).toHaveBeenCalledWith(
        examCollection,
        ...additionalExams.map(expect.objectContaining),
      );
      expect(comp.examsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const userCustom: IUserCustom = { id: 456 };
      const country: ICountry = { id: 30189 };
      userCustom.country = country;
      const job: IJob = { id: 5147 };
      userCustom.job = job;
      const exam: IExam = { id: 8935 };
      userCustom.exams = [exam];

      activatedRoute.data = of({ userCustom });
      comp.ngOnInit();

      expect(comp.countriesSharedCollection).toContain(country);
      expect(comp.jobsSharedCollection).toContain(job);
      expect(comp.examsSharedCollection).toContain(exam);
      expect(comp.userCustom).toEqual(userCustom);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserCustom>>();
      const userCustom = { id: 123 };
      jest.spyOn(userCustomFormService, 'getUserCustom').mockReturnValue(userCustom);
      jest.spyOn(userCustomService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userCustom });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userCustom }));
      saveSubject.complete();

      // THEN
      expect(userCustomFormService.getUserCustom).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(userCustomService.update).toHaveBeenCalledWith(expect.objectContaining(userCustom));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserCustom>>();
      const userCustom = { id: 123 };
      jest.spyOn(userCustomFormService, 'getUserCustom').mockReturnValue({ id: null });
      jest.spyOn(userCustomService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userCustom: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: userCustom }));
      saveSubject.complete();

      // THEN
      expect(userCustomFormService.getUserCustom).toHaveBeenCalled();
      expect(userCustomService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IUserCustom>>();
      const userCustom = { id: 123 };
      jest.spyOn(userCustomService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ userCustom });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(userCustomService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCountry', () => {
      it('Should forward to countryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(countryService, 'compareCountry');
        comp.compareCountry(entity, entity2);
        expect(countryService.compareCountry).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareJob', () => {
      it('Should forward to jobService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(jobService, 'compareJob');
        comp.compareJob(entity, entity2);
        expect(jobService.compareJob).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareExam', () => {
      it('Should forward to examService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(examService, 'compareExam');
        comp.compareExam(entity, entity2);
        expect(examService.compareExam).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
