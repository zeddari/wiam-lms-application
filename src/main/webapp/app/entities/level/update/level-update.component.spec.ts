import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LevelService } from '../service/level.service';
import { ILevel } from '../level.model';
import { LevelFormService } from './level-form.service';

import { LevelUpdateComponent } from './level-update.component';

describe('Level Management Update Component', () => {
  let comp: LevelUpdateComponent;
  let fixture: ComponentFixture<LevelUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let levelFormService: LevelFormService;
  let levelService: LevelService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), LevelUpdateComponent],
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
      .overrideTemplate(LevelUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LevelUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    levelFormService = TestBed.inject(LevelFormService);
    levelService = TestBed.inject(LevelService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const level: ILevel = { id: 456 };

      activatedRoute.data = of({ level });
      comp.ngOnInit();

      expect(comp.level).toEqual(level);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILevel>>();
      const level = { id: 123 };
      jest.spyOn(levelFormService, 'getLevel').mockReturnValue(level);
      jest.spyOn(levelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ level });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: level }));
      saveSubject.complete();

      // THEN
      expect(levelFormService.getLevel).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(levelService.update).toHaveBeenCalledWith(expect.objectContaining(level));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILevel>>();
      const level = { id: 123 };
      jest.spyOn(levelFormService, 'getLevel').mockReturnValue({ id: null });
      jest.spyOn(levelService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ level: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: level }));
      saveSubject.complete();

      // THEN
      expect(levelFormService.getLevel).toHaveBeenCalled();
      expect(levelService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILevel>>();
      const level = { id: 123 };
      jest.spyOn(levelService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ level });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(levelService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
