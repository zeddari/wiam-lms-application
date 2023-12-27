import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DiplomaTypeService } from '../service/diploma-type.service';
import { IDiplomaType } from '../diploma-type.model';
import { DiplomaTypeFormService } from './diploma-type-form.service';

import { DiplomaTypeUpdateComponent } from './diploma-type-update.component';

describe('DiplomaType Management Update Component', () => {
  let comp: DiplomaTypeUpdateComponent;
  let fixture: ComponentFixture<DiplomaTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let diplomaTypeFormService: DiplomaTypeFormService;
  let diplomaTypeService: DiplomaTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), DiplomaTypeUpdateComponent],
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
      .overrideTemplate(DiplomaTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DiplomaTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    diplomaTypeFormService = TestBed.inject(DiplomaTypeFormService);
    diplomaTypeService = TestBed.inject(DiplomaTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const diplomaType: IDiplomaType = { id: 456 };

      activatedRoute.data = of({ diplomaType });
      comp.ngOnInit();

      expect(comp.diplomaType).toEqual(diplomaType);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiplomaType>>();
      const diplomaType = { id: 123 };
      jest.spyOn(diplomaTypeFormService, 'getDiplomaType').mockReturnValue(diplomaType);
      jest.spyOn(diplomaTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diplomaType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: diplomaType }));
      saveSubject.complete();

      // THEN
      expect(diplomaTypeFormService.getDiplomaType).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(diplomaTypeService.update).toHaveBeenCalledWith(expect.objectContaining(diplomaType));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiplomaType>>();
      const diplomaType = { id: 123 };
      jest.spyOn(diplomaTypeFormService, 'getDiplomaType').mockReturnValue({ id: null });
      jest.spyOn(diplomaTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diplomaType: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: diplomaType }));
      saveSubject.complete();

      // THEN
      expect(diplomaTypeFormService.getDiplomaType).toHaveBeenCalled();
      expect(diplomaTypeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiplomaType>>();
      const diplomaType = { id: 123 };
      jest.spyOn(diplomaTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diplomaType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(diplomaTypeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
