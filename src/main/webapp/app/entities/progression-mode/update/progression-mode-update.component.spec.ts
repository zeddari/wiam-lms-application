import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProgressionModeService } from '../service/progression-mode.service';
import { IProgressionMode } from '../progression-mode.model';
import { ProgressionModeFormService } from './progression-mode-form.service';

import { ProgressionModeUpdateComponent } from './progression-mode-update.component';

describe('ProgressionMode Management Update Component', () => {
  let comp: ProgressionModeUpdateComponent;
  let fixture: ComponentFixture<ProgressionModeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let progressionModeFormService: ProgressionModeFormService;
  let progressionModeService: ProgressionModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), ProgressionModeUpdateComponent],
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
      .overrideTemplate(ProgressionModeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgressionModeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    progressionModeFormService = TestBed.inject(ProgressionModeFormService);
    progressionModeService = TestBed.inject(ProgressionModeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const progressionMode: IProgressionMode = { id: 456 };

      activatedRoute.data = of({ progressionMode });
      comp.ngOnInit();

      expect(comp.progressionMode).toEqual(progressionMode);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgressionMode>>();
      const progressionMode = { id: 123 };
      jest.spyOn(progressionModeFormService, 'getProgressionMode').mockReturnValue(progressionMode);
      jest.spyOn(progressionModeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ progressionMode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: progressionMode }));
      saveSubject.complete();

      // THEN
      expect(progressionModeFormService.getProgressionMode).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(progressionModeService.update).toHaveBeenCalledWith(expect.objectContaining(progressionMode));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgressionMode>>();
      const progressionMode = { id: 123 };
      jest.spyOn(progressionModeFormService, 'getProgressionMode').mockReturnValue({ id: null });
      jest.spyOn(progressionModeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ progressionMode: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: progressionMode }));
      saveSubject.complete();

      // THEN
      expect(progressionModeFormService.getProgressionMode).toHaveBeenCalled();
      expect(progressionModeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProgressionMode>>();
      const progressionMode = { id: 123 };
      jest.spyOn(progressionModeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ progressionMode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(progressionModeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
