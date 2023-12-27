import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TicketsService } from '../service/tickets.service';

import { TicketsComponent } from './tickets.component';

describe('Tickets Management Component', () => {
  let comp: TicketsComponent;
  let fixture: ComponentFixture<TicketsComponent>;
  let service: TicketsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'tickets', component: TicketsComponent }]),
        HttpClientTestingModule,
        TicketsComponent,
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
      .overrideTemplate(TicketsComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TicketsComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TicketsService);

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
    expect(comp.tickets?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to ticketsService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getTicketsIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getTicketsIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
