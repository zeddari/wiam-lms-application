import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SessionModeService } from '../service/session-mode.service';
import { ISessionMode } from '../session-mode.model';
import { SessionModeFormService } from './session-mode-form.service';

import { SessionModeUpdateComponent } from './session-mode-update.component';

describe('SessionMode Management Update Component', () => {
  let comp: SessionModeUpdateComponent;
  let fixture: ComponentFixture<SessionModeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sessionModeFormService: SessionModeFormService;
  let sessionModeService: SessionModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SessionModeUpdateComponent],
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
      .overrideTemplate(SessionModeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionModeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sessionModeFormService = TestBed.inject(SessionModeFormService);
    sessionModeService = TestBed.inject(SessionModeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const sessionMode: ISessionMode = { id: 456 };

      activatedRoute.data = of({ sessionMode });
      comp.ngOnInit();

      expect(comp.sessionMode).toEqual(sessionMode);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionMode>>();
      const sessionMode = { id: 123 };
      jest.spyOn(sessionModeFormService, 'getSessionMode').mockReturnValue(sessionMode);
      jest.spyOn(sessionModeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionMode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionMode }));
      saveSubject.complete();

      // THEN
      expect(sessionModeFormService.getSessionMode).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sessionModeService.update).toHaveBeenCalledWith(expect.objectContaining(sessionMode));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionMode>>();
      const sessionMode = { id: 123 };
      jest.spyOn(sessionModeFormService, 'getSessionMode').mockReturnValue({ id: null });
      jest.spyOn(sessionModeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionMode: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionMode }));
      saveSubject.complete();

      // THEN
      expect(sessionModeFormService.getSessionMode).toHaveBeenCalled();
      expect(sessionModeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionMode>>();
      const sessionMode = { id: 123 };
      jest.spyOn(sessionModeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionMode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sessionModeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
