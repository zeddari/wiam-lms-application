import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SessionProviderService } from '../service/session-provider.service';
import { ISessionProvider } from '../session-provider.model';
import { SessionProviderFormService } from './session-provider-form.service';

import { SessionProviderUpdateComponent } from './session-provider-update.component';

describe('SessionProvider Management Update Component', () => {
  let comp: SessionProviderUpdateComponent;
  let fixture: ComponentFixture<SessionProviderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sessionProviderFormService: SessionProviderFormService;
  let sessionProviderService: SessionProviderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SessionProviderUpdateComponent],
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
      .overrideTemplate(SessionProviderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionProviderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sessionProviderFormService = TestBed.inject(SessionProviderFormService);
    sessionProviderService = TestBed.inject(SessionProviderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const sessionProvider: ISessionProvider = { id: 456 };

      activatedRoute.data = of({ sessionProvider });
      comp.ngOnInit();

      expect(comp.sessionProvider).toEqual(sessionProvider);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionProvider>>();
      const sessionProvider = { id: 123 };
      jest.spyOn(sessionProviderFormService, 'getSessionProvider').mockReturnValue(sessionProvider);
      jest.spyOn(sessionProviderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionProvider });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionProvider }));
      saveSubject.complete();

      // THEN
      expect(sessionProviderFormService.getSessionProvider).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sessionProviderService.update).toHaveBeenCalledWith(expect.objectContaining(sessionProvider));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionProvider>>();
      const sessionProvider = { id: 123 };
      jest.spyOn(sessionProviderFormService, 'getSessionProvider').mockReturnValue({ id: null });
      jest.spyOn(sessionProviderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionProvider: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionProvider }));
      saveSubject.complete();

      // THEN
      expect(sessionProviderFormService.getSessionProvider).toHaveBeenCalled();
      expect(sessionProviderService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionProvider>>();
      const sessionProvider = { id: 123 };
      jest.spyOn(sessionProviderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionProvider });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sessionProviderService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
