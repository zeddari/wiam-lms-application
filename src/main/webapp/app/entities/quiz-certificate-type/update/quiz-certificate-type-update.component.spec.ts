import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { QuizCertificateTypeService } from '../service/quiz-certificate-type.service';
import { IQuizCertificateType } from '../quiz-certificate-type.model';
import { QuizCertificateTypeFormService } from './quiz-certificate-type-form.service';

import { QuizCertificateTypeUpdateComponent } from './quiz-certificate-type-update.component';

describe('QuizCertificateType Management Update Component', () => {
  let comp: QuizCertificateTypeUpdateComponent;
  let fixture: ComponentFixture<QuizCertificateTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let quizCertificateTypeFormService: QuizCertificateTypeFormService;
  let quizCertificateTypeService: QuizCertificateTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), QuizCertificateTypeUpdateComponent],
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
      .overrideTemplate(QuizCertificateTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuizCertificateTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    quizCertificateTypeFormService = TestBed.inject(QuizCertificateTypeFormService);
    quizCertificateTypeService = TestBed.inject(QuizCertificateTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const quizCertificateType: IQuizCertificateType = { id: 456 };

      activatedRoute.data = of({ quizCertificateType });
      comp.ngOnInit();

      expect(comp.quizCertificateType).toEqual(quizCertificateType);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuizCertificateType>>();
      const quizCertificateType = { id: 123 };
      jest.spyOn(quizCertificateTypeFormService, 'getQuizCertificateType').mockReturnValue(quizCertificateType);
      jest.spyOn(quizCertificateTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quizCertificateType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quizCertificateType }));
      saveSubject.complete();

      // THEN
      expect(quizCertificateTypeFormService.getQuizCertificateType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(quizCertificateTypeService.update).toHaveBeenCalledWith(expect.objectContaining(quizCertificateType));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuizCertificateType>>();
      const quizCertificateType = { id: 123 };
      jest.spyOn(quizCertificateTypeFormService, 'getQuizCertificateType').mockReturnValue({ id: null });
      jest.spyOn(quizCertificateTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quizCertificateType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quizCertificateType }));
      saveSubject.complete();

      // THEN
      expect(quizCertificateTypeFormService.getQuizCertificateType).toHaveBeenCalled();
      expect(quizCertificateTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuizCertificateType>>();
      const quizCertificateType = { id: 123 };
      jest.spyOn(quizCertificateTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quizCertificateType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(quizCertificateTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
