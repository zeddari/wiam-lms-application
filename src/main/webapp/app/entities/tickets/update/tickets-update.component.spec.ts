import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IUserCustom } from 'app/entities/user-custom/user-custom.model';
import { UserCustomService } from 'app/entities/user-custom/service/user-custom.service';
import { ITicketSubjects } from 'app/entities/ticket-subjects/ticket-subjects.model';
import { TicketSubjectsService } from 'app/entities/ticket-subjects/service/ticket-subjects.service';
import { ITickets } from '../tickets.model';
import { TicketsService } from '../service/tickets.service';
import { TicketsFormService } from './tickets-form.service';

import { TicketsUpdateComponent } from './tickets-update.component';

describe('Tickets Management Update Component', () => {
  let comp: TicketsUpdateComponent;
  let fixture: ComponentFixture<TicketsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ticketsFormService: TicketsFormService;
  let ticketsService: TicketsService;
  let userCustomService: UserCustomService;
  let ticketSubjectsService: TicketSubjectsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TicketsUpdateComponent],
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
      .overrideTemplate(TicketsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TicketsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ticketsFormService = TestBed.inject(TicketsFormService);
    ticketsService = TestBed.inject(TicketsService);
    userCustomService = TestBed.inject(UserCustomService);
    ticketSubjectsService = TestBed.inject(TicketSubjectsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call UserCustom query and add missing value', () => {
      const tickets: ITickets = { id: 456 };
      const userCustom: IUserCustom = { id: 4621 };
      tickets.userCustom = userCustom;

      const userCustomCollection: IUserCustom[] = [{ id: 26463 }];
      jest.spyOn(userCustomService, 'query').mockReturnValue(of(new HttpResponse({ body: userCustomCollection })));
      const additionalUserCustoms = [userCustom];
      const expectedCollection: IUserCustom[] = [...additionalUserCustoms, ...userCustomCollection];
      jest.spyOn(userCustomService, 'addUserCustomToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tickets });
      comp.ngOnInit();

      expect(userCustomService.query).toHaveBeenCalled();
      expect(userCustomService.addUserCustomToCollectionIfMissing).toHaveBeenCalledWith(
        userCustomCollection,
        ...additionalUserCustoms.map(expect.objectContaining),
      );
      expect(comp.userCustomsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TicketSubjects query and add missing value', () => {
      const tickets: ITickets = { id: 456 };
      const subject: ITicketSubjects = { id: 6583 };
      tickets.subject = subject;

      const ticketSubjectsCollection: ITicketSubjects[] = [{ id: 17487 }];
      jest.spyOn(ticketSubjectsService, 'query').mockReturnValue(of(new HttpResponse({ body: ticketSubjectsCollection })));
      const additionalTicketSubjects = [subject];
      const expectedCollection: ITicketSubjects[] = [...additionalTicketSubjects, ...ticketSubjectsCollection];
      jest.spyOn(ticketSubjectsService, 'addTicketSubjectsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ tickets });
      comp.ngOnInit();

      expect(ticketSubjectsService.query).toHaveBeenCalled();
      expect(ticketSubjectsService.addTicketSubjectsToCollectionIfMissing).toHaveBeenCalledWith(
        ticketSubjectsCollection,
        ...additionalTicketSubjects.map(expect.objectContaining),
      );
      expect(comp.ticketSubjectsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const tickets: ITickets = { id: 456 };
      const userCustom: IUserCustom = { id: 17261 };
      tickets.userCustom = userCustom;
      const subject: ITicketSubjects = { id: 32385 };
      tickets.subject = subject;

      activatedRoute.data = of({ tickets });
      comp.ngOnInit();

      expect(comp.userCustomsSharedCollection).toContain(userCustom);
      expect(comp.ticketSubjectsSharedCollection).toContain(subject);
      expect(comp.tickets).toEqual(tickets);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITickets>>();
      const tickets = { id: 123 };
      jest.spyOn(ticketsFormService, 'getTickets').mockReturnValue(tickets);
      jest.spyOn(ticketsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tickets });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tickets }));
      saveSubject.complete();

      // THEN
      expect(ticketsFormService.getTickets).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ticketsService.update).toHaveBeenCalledWith(expect.objectContaining(tickets));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITickets>>();
      const tickets = { id: 123 };
      jest.spyOn(ticketsFormService, 'getTickets').mockReturnValue({ id: null });
      jest.spyOn(ticketsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tickets: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tickets }));
      saveSubject.complete();

      // THEN
      expect(ticketsFormService.getTickets).toHaveBeenCalled();
      expect(ticketsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITickets>>();
      const tickets = { id: 123 };
      jest.spyOn(ticketsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tickets });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ticketsService.update).toHaveBeenCalled();
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

    describe('compareTicketSubjects', () => {
      it('Should forward to ticketSubjectsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(ticketSubjectsService, 'compareTicketSubjects');
        comp.compareTicketSubjects(entity, entity2);
        expect(ticketSubjectsService.compareTicketSubjects).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
