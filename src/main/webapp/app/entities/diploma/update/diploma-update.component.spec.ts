import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { UserCustomService } from 'app/entities/user-custom/service/user-custom.service';
import { IDiplomaType } from 'app/entities/diploma-type/diploma-type.model';
import { DiplomaTypeService } from 'app/entities/diploma-type/service/diploma-type.service';
import { IDiploma } from '../diploma.model';
import { DiplomaService } from '../service/diploma.service';
import { DiplomaFormService } from './diploma-form.service';

import { DiplomaUpdateComponent } from './diploma-update.component';

describe('Diploma Management Update Component', () => {
  let comp: DiplomaUpdateComponent;
  let fixture: ComponentFixture<DiplomaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let diplomaFormService: DiplomaFormService;
  let diplomaService: DiplomaService;
  let userCustomService: UserCustomService;
  let diplomaTypeService: DiplomaTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), DiplomaUpdateComponent],
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
      .overrideTemplate(DiplomaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DiplomaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    diplomaFormService = TestBed.inject(DiplomaFormService);
    diplomaService = TestBed.inject(DiplomaService);
    userCustomService = TestBed.inject(UserCustomService);
    diplomaTypeService = TestBed.inject(DiplomaTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserCustom query and add missing value', () => {
      const diploma: IDiploma = { id: 456 };
      const userCustom: IUserCustom = { id: 7527 };
      diploma.userCustom = userCustom;

      const userCustomCollection: IUserCustom[] = [{ id: 32548 }];
      jest.spyOn(userCustomService, 'query').mockReturnValue(of(new HttpResponse({ body: userCustomCollection })));
      const additionalUserCustoms = [userCustom];
      const expectedCollection: IUserCustom[] = [...additionalUserCustoms, ...userCustomCollection];
      jest.spyOn(userCustomService, 'addUserCustomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ diploma });
      comp.ngOnInit();

      expect(userCustomService.query).toHaveBeenCalled();
      expect(userCustomService.addUserCustomToCollectionIfMissing).toHaveBeenCalledWith(
        userCustomCollection,
        ...additionalUserCustoms.map(expect.objectContaining),
      );
      expect(comp.userCustomsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call DiplomaType query and add missing value', () => {
      const diploma: IDiploma = { id: 456 };
      const diplomaType: IDiplomaType = { id: 15099 };
      diploma.diplomaType = diplomaType;

      const diplomaTypeCollection: IDiplomaType[] = [{ id: 4067 }];
      jest.spyOn(diplomaTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: diplomaTypeCollection })));
      const additionalDiplomaTypes = [diplomaType];
      const expectedCollection: IDiplomaType[] = [...additionalDiplomaTypes, ...diplomaTypeCollection];
      jest.spyOn(diplomaTypeService, 'addDiplomaTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ diploma });
      comp.ngOnInit();

      expect(diplomaTypeService.query).toHaveBeenCalled();
      expect(diplomaTypeService.addDiplomaTypeToCollectionIfMissing).toHaveBeenCalledWith(
        diplomaTypeCollection,
        ...additionalDiplomaTypes.map(expect.objectContaining),
      );
      expect(comp.diplomaTypesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const diploma: IDiploma = { id: 456 };
      const userCustom: IUserCustom = { id: 9640 };
      diploma.userCustom = userCustom;
      const diplomaType: IDiplomaType = { id: 13725 };
      diploma.diplomaType = diplomaType;

      activatedRoute.data = of({ diploma });
      comp.ngOnInit();

      expect(comp.userCustomsSharedCollection).toContain(userCustom);
      expect(comp.diplomaTypesSharedCollection).toContain(diplomaType);
      expect(comp.diploma).toEqual(diploma);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiploma>>();
      const diploma = { id: 123 };
      jest.spyOn(diplomaFormService, 'getDiploma').mockReturnValue(diploma);
      jest.spyOn(diplomaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diploma });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: diploma }));
      saveSubject.complete();

      // THEN
      expect(diplomaFormService.getDiploma).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(diplomaService.update).toHaveBeenCalledWith(expect.objectContaining(diploma));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiploma>>();
      const diploma = { id: 123 };
      jest.spyOn(diplomaFormService, 'getDiploma').mockReturnValue({ id: null });
      jest.spyOn(diplomaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diploma: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: diploma }));
      saveSubject.complete();

      // THEN
      expect(diplomaFormService.getDiploma).toHaveBeenCalled();
      expect(diplomaService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDiploma>>();
      const diploma = { id: 123 };
      jest.spyOn(diplomaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ diploma });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(diplomaService.update).toHaveBeenCalled();
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

    describe('compareDiplomaType', () => {
      it('Should forward to diplomaTypeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(diplomaTypeService, 'compareDiplomaType');
        comp.compareDiplomaType(entity, entity2);
        expect(diplomaTypeService.compareDiplomaType).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
