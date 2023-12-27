import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { Country2Service } from '../service/country-2.service';
import { ICountry2 } from '../country-2.model';
import { Country2FormService } from './country-2-form.service';

import { Country2UpdateComponent } from './country-2-update.component';

describe('Country2 Management Update Component', () => {
  let comp: Country2UpdateComponent;
  let fixture: ComponentFixture<Country2UpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let country2FormService: Country2FormService;
  let country2Service: Country2Service;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), Country2UpdateComponent],
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
      .overrideTemplate(Country2UpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(Country2UpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    country2FormService = TestBed.inject(Country2FormService);
    country2Service = TestBed.inject(Country2Service);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const country2: ICountry2 = { id: 456 };

      activatedRoute.data = of({ country2 });
      comp.ngOnInit();

      expect(comp.country2).toEqual(country2);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICountry2>>();
      const country2 = { id: 123 };
      jest.spyOn(country2FormService, 'getCountry2').mockReturnValue(country2);
      jest.spyOn(country2Service, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ country2 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: country2 }));
      saveSubject.complete();

      // THEN
      expect(country2FormService.getCountry2).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(country2Service.update).toHaveBeenCalledWith(expect.objectContaining(country2));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICountry2>>();
      const country2 = { id: 123 };
      jest.spyOn(country2FormService, 'getCountry2').mockReturnValue({ id: null });
      jest.spyOn(country2Service, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ country2: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: country2 }));
      saveSubject.complete();

      // THEN
      expect(country2FormService.getCountry2).toHaveBeenCalled();
      expect(country2Service.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICountry2>>();
      const country2 = { id: 123 };
      jest.spyOn(country2Service, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ country2 });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(country2Service.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
