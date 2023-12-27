import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SessionJoinModeService } from '../service/session-join-mode.service';
import { ISessionJoinMode } from '../session-join-mode.model';
import { SessionJoinModeFormService } from './session-join-mode-form.service';

import { SessionJoinModeUpdateComponent } from './session-join-mode-update.component';

describe('SessionJoinMode Management Update Component', () => {
  let comp: SessionJoinModeUpdateComponent;
  let fixture: ComponentFixture<SessionJoinModeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sessionJoinModeFormService: SessionJoinModeFormService;
  let sessionJoinModeService: SessionJoinModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SessionJoinModeUpdateComponent],
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
      .overrideTemplate(SessionJoinModeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionJoinModeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sessionJoinModeFormService = TestBed.inject(SessionJoinModeFormService);
    sessionJoinModeService = TestBed.inject(SessionJoinModeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const sessionJoinMode: ISessionJoinMode = { id: 456 };

      activatedRoute.data = of({ sessionJoinMode });
      comp.ngOnInit();

      expect(comp.sessionJoinMode).toEqual(sessionJoinMode);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionJoinMode>>();
      const sessionJoinMode = { id: 123 };
      jest.spyOn(sessionJoinModeFormService, 'getSessionJoinMode').mockReturnValue(sessionJoinMode);
      jest.spyOn(sessionJoinModeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionJoinMode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionJoinMode }));
      saveSubject.complete();

      // THEN
      expect(sessionJoinModeFormService.getSessionJoinMode).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sessionJoinModeService.update).toHaveBeenCalledWith(expect.objectContaining(sessionJoinMode));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionJoinMode>>();
      const sessionJoinMode = { id: 123 };
      jest.spyOn(sessionJoinModeFormService, 'getSessionJoinMode').mockReturnValue({ id: null });
      jest.spyOn(sessionJoinModeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionJoinMode: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionJoinMode }));
      saveSubject.complete();

      // THEN
      expect(sessionJoinModeFormService.getSessionJoinMode).toHaveBeenCalled();
      expect(sessionJoinModeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionJoinMode>>();
      const sessionJoinMode = { id: 123 };
      jest.spyOn(sessionJoinModeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionJoinMode });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sessionJoinModeService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
