import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionModeService } from '../service/session-mode.service';

import { SessionModeComponent } from './session-mode.component';

describe('SessionMode Management Component', () => {
  let comp: SessionModeComponent;
  let fixture: ComponentFixture<SessionModeComponent>;
  let service: SessionModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'session-mode', component: SessionModeComponent }]),
        HttpClientTestingModule,
        SessionModeComponent,
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
      .overrideTemplate(SessionModeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionModeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SessionModeService);

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
    expect(comp.sessionModes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to sessionModeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSessionModeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSessionModeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
