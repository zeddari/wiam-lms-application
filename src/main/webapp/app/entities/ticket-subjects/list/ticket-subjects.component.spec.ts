import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TicketSubjectsService } from '../service/ticket-subjects.service';

import { TicketSubjectsComponent } from './ticket-subjects.component';

describe('TicketSubjects Management Component', () => {
  let comp: TicketSubjectsComponent;
  let fixture: ComponentFixture<TicketSubjectsComponent>;
  let service: TicketSubjectsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'ticket-subjects', component: TicketSubjectsComponent }]),
        HttpClientTestingModule,
        TicketSubjectsComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(TicketSubjectsComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TicketSubjectsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TicketSubjectsService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        }),
      ),
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.ticketSubjects?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to ticketSubjectsService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTicketSubjectsIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTicketSubjectsIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
