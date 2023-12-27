import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { LeaveRequestService } from '../service/leave-request.service';

import { LeaveRequestComponent } from './leave-request.component';

describe('LeaveRequest Management Component', () => {
  let comp: LeaveRequestComponent;
  let fixture: ComponentFixture<LeaveRequestComponent>;
  let service: LeaveRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'leave-request', component: LeaveRequestComponent }]),
        HttpClientTestingModule,
        LeaveRequestComponent,
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
      .overrideTemplate(LeaveRequestComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LeaveRequestComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(LeaveRequestService);

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
    expect(comp.leaveRequests?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to leaveRequestService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getLeaveRequestIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getLeaveRequestIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
