import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { JobTitleService } from '../service/job-title.service';
import { IJobTitle } from '../job-title.model';
import { JobTitleFormService } from './job-title-form.service';

import { JobTitleUpdateComponent } from './job-title-update.component';

describe('JobTitle Management Update Component', () => {
  let comp: JobTitleUpdateComponent;
  let fixture: ComponentFixture<JobTitleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let jobTitleFormService: JobTitleFormService;
  let jobTitleService: JobTitleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), JobTitleUpdateComponent],
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
      .overrideTemplate(JobTitleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JobTitleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    jobTitleFormService = TestBed.inject(JobTitleFormService);
    jobTitleService = TestBed.inject(JobTitleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const jobTitle: IJobTitle = { id: 456 };

      activatedRoute.data = of({ jobTitle });
      comp.ngOnInit();

      expect(comp.jobTitle).toEqual(jobTitle);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJobTitle>>();
      const jobTitle = { id: 123 };
      jest.spyOn(jobTitleFormService, 'getJobTitle').mockReturnValue(jobTitle);
      jest.spyOn(jobTitleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobTitle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jobTitle }));
      saveSubject.complete();

      // THEN
      expect(jobTitleFormService.getJobTitle).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(jobTitleService.update).toHaveBeenCalledWith(expect.objectContaining(jobTitle));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJobTitle>>();
      const jobTitle = { id: 123 };
      jest.spyOn(jobTitleFormService, 'getJobTitle').mockReturnValue({ id: null });
      jest.spyOn(jobTitleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobTitle: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jobTitle }));
      saveSubject.complete();

      // THEN
      expect(jobTitleFormService.getJobTitle).toHaveBeenCalled();
      expect(jobTitleService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJobTitle>>();
      const jobTitle = { id: 123 };
      jest.spyOn(jobTitleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobTitle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(jobTitleService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
