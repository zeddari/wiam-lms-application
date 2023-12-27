import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TicketSubjectsService } from '../service/ticket-subjects.service';
import { ITicketSubjects } from '../ticket-subjects.model';
import { TicketSubjectsFormService } from './ticket-subjects-form.service';

import { TicketSubjectsUpdateComponent } from './ticket-subjects-update.component';

describe('TicketSubjects Management Update Component', () => {
  let comp: TicketSubjectsUpdateComponent;
  let fixture: ComponentFixture<TicketSubjectsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ticketSubjectsFormService: TicketSubjectsFormService;
  let ticketSubjectsService: TicketSubjectsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TicketSubjectsUpdateComponent],
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
      .overrideTemplate(TicketSubjectsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TicketSubjectsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ticketSubjectsFormService = TestBed.inject(TicketSubjectsFormService);
    ticketSubjectsService = TestBed.inject(TicketSubjectsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const ticketSubjects: ITicketSubjects = { id: 456 };

      activatedRoute.data = of({ ticketSubjects });
      comp.ngOnInit();

      expect(comp.ticketSubjects).toEqual(ticketSubjects);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITicketSubjects>>();
      const ticketSubjects = { id: 123 };
      jest.spyOn(ticketSubjectsFormService, 'getTicketSubjects').mockReturnValue(ticketSubjects);
      jest.spyOn(ticketSubjectsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketSubjects });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ticketSubjects }));
      saveSubject.complete();

      // THEN
      expect(ticketSubjectsFormService.getTicketSubjects).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ticketSubjectsService.update).toHaveBeenCalledWith(expect.objectContaining(ticketSubjects));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITicketSubjects>>();
      const ticketSubjects = { id: 123 };
      jest.spyOn(ticketSubjectsFormService, 'getTicketSubjects').mockReturnValue({ id: null });
      jest.spyOn(ticketSubjectsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketSubjects: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ticketSubjects }));
      saveSubject.complete();

      // THEN
      expect(ticketSubjectsFormService.getTicketSubjects).toHaveBeenCalled();
      expect(ticketSubjectsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITicketSubjects>>();
      const ticketSubjects = { id: 123 };
      jest.spyOn(ticketSubjectsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticketSubjects });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ticketSubjectsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
