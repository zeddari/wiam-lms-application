import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { UserCustomService } from 'app/entities/user-custom/service/user-custom.service';
import { LanguageService } from '../service/language.service';
import { ILanguage } from '../language.model';
import { LanguageFormService } from './language-form.service';

import { LanguageUpdateComponent } from './language-update.component';

describe('Language Management Update Component', () => {
  let comp: LanguageUpdateComponent;
  let fixture: ComponentFixture<LanguageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let languageFormService: LanguageFormService;
  let languageService: LanguageService;
  let userCustomService: UserCustomService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), LanguageUpdateComponent],
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
      .overrideTemplate(LanguageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LanguageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    languageFormService = TestBed.inject(LanguageFormService);
    languageService = TestBed.inject(LanguageService);
    userCustomService = TestBed.inject(UserCustomService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserCustom query and add missing value', () => {
      const language: ILanguage = { id: 456 };
      const userCustom: IUserCustom = { id: 26538 };
      language.userCustom = userCustom;

      const userCustomCollection: IUserCustom[] = [{ id: 30415 }];
      jest.spyOn(userCustomService, 'query').mockReturnValue(of(new HttpResponse({ body: userCustomCollection })));
      const additionalUserCustoms = [userCustom];
      const expectedCollection: IUserCustom[] = [...additionalUserCustoms, ...userCustomCollection];
      jest.spyOn(userCustomService, 'addUserCustomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ language });
      comp.ngOnInit();

      expect(userCustomService.query).toHaveBeenCalled();
      expect(userCustomService.addUserCustomToCollectionIfMissing).toHaveBeenCalledWith(
        userCustomCollection,
        ...additionalUserCustoms.map(expect.objectContaining),
      );
      expect(comp.userCustomsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const language: ILanguage = { id: 456 };
      const userCustom: IUserCustom = { id: 22020 };
      language.userCustom = userCustom;

      activatedRoute.data = of({ language });
      comp.ngOnInit();

      expect(comp.userCustomsSharedCollection).toContain(userCustom);
      expect(comp.language).toEqual(language);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILanguage>>();
      const language = { id: 123 };
      jest.spyOn(languageFormService, 'getLanguage').mockReturnValue(language);
      jest.spyOn(languageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ language });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: language }));
      saveSubject.complete();

      // THEN
      expect(languageFormService.getLanguage).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(languageService.update).toHaveBeenCalledWith(expect.objectContaining(language));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILanguage>>();
      const language = { id: 123 };
      jest.spyOn(languageFormService, 'getLanguage').mockReturnValue({ id: null });
      jest.spyOn(languageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ language: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: language }));
      saveSubject.complete();

      // THEN
      expect(languageFormService.getLanguage).toHaveBeenCalled();
      expect(languageService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILanguage>>();
      const language = { id: 123 };
      jest.spyOn(languageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ language });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(languageService.update).toHaveBeenCalled();
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
  });
});
