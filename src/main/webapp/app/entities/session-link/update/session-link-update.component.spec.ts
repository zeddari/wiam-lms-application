import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ISessionProvider } from 'app/entities/session-provider/session-provider.model';
import { SessionProviderService } from 'app/entities/session-provider/service/session-provider.service';
import { SessionLinkService } from '../service/session-link.service';
import { ISessionLink } from '../session-link.model';
import { SessionLinkFormService } from './session-link-form.service';

import { SessionLinkUpdateComponent } from './session-link-update.component';

describe('SessionLink Management Update Component', () => {
  let comp: SessionLinkUpdateComponent;
  let fixture: ComponentFixture<SessionLinkUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sessionLinkFormService: SessionLinkFormService;
  let sessionLinkService: SessionLinkService;
  let sessionProviderService: SessionProviderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SessionLinkUpdateComponent],
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
      .overrideTemplate(SessionLinkUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionLinkUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sessionLinkFormService = TestBed.inject(SessionLinkFormService);
    sessionLinkService = TestBed.inject(SessionLinkService);
    sessionProviderService = TestBed.inject(SessionProviderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call SessionProvider query and add missing value', () => {
      const sessionLink: ISessionLink = { id: 456 };
      const provider: ISessionProvider = { id: 2586 };
      sessionLink.provider = provider;

      const sessionProviderCollection: ISessionProvider[] = [{ id: 13431 }];
      jest.spyOn(sessionProviderService, 'query').mockReturnValue(of(new HttpResponse({ body: sessionProviderCollection })));
      const additionalSessionProviders = [provider];
      const expectedCollection: ISessionProvider[] = [...additionalSessionProviders, ...sessionProviderCollection];
      jest.spyOn(sessionProviderService, 'addSessionProviderToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sessionLink });
      comp.ngOnInit();

      expect(sessionProviderService.query).toHaveBeenCalled();
      expect(sessionProviderService.addSessionProviderToCollectionIfMissing).toHaveBeenCalledWith(
        sessionProviderCollection,
        ...additionalSessionProviders.map(expect.objectContaining),
      );
      expect(comp.sessionProvidersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const sessionLink: ISessionLink = { id: 456 };
      const provider: ISessionProvider = { id: 519 };
      sessionLink.provider = provider;

      activatedRoute.data = of({ sessionLink });
      comp.ngOnInit();

      expect(comp.sessionProvidersSharedCollection).toContain(provider);
      expect(comp.sessionLink).toEqual(sessionLink);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionLink>>();
      const sessionLink = { id: 123 };
      jest.spyOn(sessionLinkFormService, 'getSessionLink').mockReturnValue(sessionLink);
      jest.spyOn(sessionLinkService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionLink });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionLink }));
      saveSubject.complete();

      // THEN
      expect(sessionLinkFormService.getSessionLink).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sessionLinkService.update).toHaveBeenCalledWith(expect.objectContaining(sessionLink));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionLink>>();
      const sessionLink = { id: 123 };
      jest.spyOn(sessionLinkFormService, 'getSessionLink').mockReturnValue({ id: null });
      jest.spyOn(sessionLinkService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionLink: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionLink }));
      saveSubject.complete();

      // THEN
      expect(sessionLinkFormService.getSessionLink).toHaveBeenCalled();
      expect(sessionLinkService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionLink>>();
      const sessionLink = { id: 123 };
      jest.spyOn(sessionLinkService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionLink });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sessionLinkService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSessionProvider', () => {
      it('Should forward to sessionProviderService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sessionProviderService, 'compareSessionProvider');
        comp.compareSessionProvider(entity, entity2);
        expect(sessionProviderService.compareSessionProvider).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
