import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { Question2Service } from '../service/question-2.service';
import { IQuestion2 } from '../question-2.model';
import { Question2FormService } from './question-2-form.service';

import { Question2UpdateComponent } from './question-2-update.component';

describe('Question2 Management Update Component', () => {
  let comp: Question2UpdateComponent;
  let fixture: ComponentFixture<Question2UpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let question2FormService: Question2FormService;
  let question2Service: Question2Service;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), Question2UpdateComponent],
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
      .overrideTemplate(Question2UpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(Question2UpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    question2FormService = TestBed.inject(Question2FormService);
    question2Service = TestBed.inject(Question2Service);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const question2: IQuestion2 = { id: 456 };

      activatedRoute.data = of({ question2 });
      comp.ngOnInit();

      expect(comp.question2).toEqual(question2);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuestion2>>();
      const question2 = { id: 123 };
      jest.spyOn(question2FormService, 'getQuestion2').mockReturnValue(question2);
      jest.spyOn(question2Service, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ question2 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: question2 }));
      saveSubject.complete();

      // THEN
      expect(question2FormService.getQuestion2).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(question2Service.update).toHaveBeenCalledWith(expect.objectContaining(question2));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuestion2>>();
      const question2 = { id: 123 };
      jest.spyOn(question2FormService, 'getQuestion2').mockReturnValue({ id: null });
      jest.spyOn(question2Service, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ question2: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: question2 }));
      saveSubject.complete();

      // THEN
      expect(question2FormService.getQuestion2).toHaveBeenCalled();
      expect(question2Service.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuestion2>>();
      const question2 = { id: 123 };
      jest.spyOn(question2Service, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ question2 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(question2Service.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
