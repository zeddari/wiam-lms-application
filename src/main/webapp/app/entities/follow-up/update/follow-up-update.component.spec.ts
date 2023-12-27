import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ICoteryHistory } from 'app/entities/cotery-history/cotery-history.model';
import { CoteryHistoryService } from 'app/entities/cotery-history/service/cotery-history.service';
import { FollowUpService } from '../service/follow-up.service';
import { IFollowUp } from '../follow-up.model';
import { FollowUpFormService } from './follow-up-form.service';

import { FollowUpUpdateComponent } from './follow-up-update.component';

describe('FollowUp Management Update Component', () => {
  let comp: FollowUpUpdateComponent;
  let fixture: ComponentFixture<FollowUpUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let followUpFormService: FollowUpFormService;
  let followUpService: FollowUpService;
  let coteryHistoryService: CoteryHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), FollowUpUpdateComponent],
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
      .overrideTemplate(FollowUpUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FollowUpUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    followUpFormService = TestBed.inject(FollowUpFormService);
    followUpService = TestBed.inject(FollowUpService);
    coteryHistoryService = TestBed.inject(CoteryHistoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call coteryHistory query and add missing value', () => {
      const followUp: IFollowUp = { id: 456 };
      const coteryHistory: ICoteryHistory = { id: 19692 };
      followUp.coteryHistory = coteryHistory;

      const coteryHistoryCollection: ICoteryHistory[] = [{ id: 12071 }];
      jest.spyOn(coteryHistoryService, 'query').mockReturnValue(of(new HttpResponse({ body: coteryHistoryCollection })));
      const expectedCollection: ICoteryHistory[] = [coteryHistory, ...coteryHistoryCollection];
      jest.spyOn(coteryHistoryService, 'addCoteryHistoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ followUp });
      comp.ngOnInit();

      expect(coteryHistoryService.query).toHaveBeenCalled();
      expect(coteryHistoryService.addCoteryHistoryToCollectionIfMissing).toHaveBeenCalledWith(coteryHistoryCollection, coteryHistory);
      expect(comp.coteryHistoriesCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const followUp: IFollowUp = { id: 456 };
      const coteryHistory: ICoteryHistory = { id: 11165 };
      followUp.coteryHistory = coteryHistory;

      activatedRoute.data = of({ followUp });
      comp.ngOnInit();

      expect(comp.coteryHistoriesCollection).toContain(coteryHistory);
      expect(comp.followUp).toEqual(followUp);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFollowUp>>();
      const followUp = { id: 123 };
      jest.spyOn(followUpFormService, 'getFollowUp').mockReturnValue(followUp);
      jest.spyOn(followUpService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ followUp });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: followUp }));
      saveSubject.complete();

      // THEN
      expect(followUpFormService.getFollowUp).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(followUpService.update).toHaveBeenCalledWith(expect.objectContaining(followUp));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFollowUp>>();
      const followUp = { id: 123 };
      jest.spyOn(followUpFormService, 'getFollowUp').mockReturnValue({ id: null });
      jest.spyOn(followUpService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ followUp: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: followUp }));
      saveSubject.complete();

      // THEN
      expect(followUpFormService.getFollowUp).toHaveBeenCalled();
      expect(followUpService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFollowUp>>();
      const followUp = { id: 123 };
      jest.spyOn(followUpService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ followUp });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(followUpService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCoteryHistory', () => {
      it('Should forward to coteryHistoryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(coteryHistoryService, 'compareCoteryHistory');
        comp.compareCoteryHistory(entity, entity2);
        expect(coteryHistoryService.compareCoteryHistory).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
