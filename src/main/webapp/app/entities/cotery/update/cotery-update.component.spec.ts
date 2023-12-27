import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CoteryService } from '../service/cotery.service';
import { ICotery } from '../cotery.model';
import { CoteryFormService } from './cotery-form.service';

import { CoteryUpdateComponent } from './cotery-update.component';

describe('Cotery Management Update Component', () => {
  let comp: CoteryUpdateComponent;
  let fixture: ComponentFixture<CoteryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let coteryFormService: CoteryFormService;
  let coteryService: CoteryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), CoteryUpdateComponent],
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
      .overrideTemplate(CoteryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CoteryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    coteryFormService = TestBed.inject(CoteryFormService);
    coteryService = TestBed.inject(CoteryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const cotery: ICotery = { id: 456 };

      activatedRoute.data = of({ cotery });
      comp.ngOnInit();

      expect(comp.cotery).toEqual(cotery);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICotery>>();
      const cotery = { id: 123 };
      jest.spyOn(coteryFormService, 'getCotery').mockReturnValue(cotery);
      jest.spyOn(coteryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cotery });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cotery }));
      saveSubject.complete();

      // THEN
      expect(coteryFormService.getCotery).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(coteryService.update).toHaveBeenCalledWith(expect.objectContaining(cotery));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICotery>>();
      const cotery = { id: 123 };
      jest.spyOn(coteryFormService, 'getCotery').mockReturnValue({ id: null });
      jest.spyOn(coteryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cotery: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cotery }));
      saveSubject.complete();

      // THEN
      expect(coteryFormService.getCotery).toHaveBeenCalled();
      expect(coteryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICotery>>();
      const cotery = { id: 123 };
      jest.spyOn(coteryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cotery });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(coteryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
