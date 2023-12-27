import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionJoinModeService } from '../service/session-join-mode.service';

import { SessionJoinModeComponent } from './session-join-mode.component';

describe('SessionJoinMode Management Component', () => {
  let comp: SessionJoinModeComponent;
  let fixture: ComponentFixture<SessionJoinModeComponent>;
  let service: SessionJoinModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'session-join-mode', component: SessionJoinModeComponent }]),
        HttpClientTestingModule,
        SessionJoinModeComponent,
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
      .overrideTemplate(SessionJoinModeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionJoinModeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SessionJoinModeService);

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
    expect(comp.sessionJoinModes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to sessionJoinModeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSessionJoinModeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSessionJoinModeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
