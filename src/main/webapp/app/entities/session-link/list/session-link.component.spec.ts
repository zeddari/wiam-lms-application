import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionLinkService } from '../service/session-link.service';

import { SessionLinkComponent } from './session-link.component';

describe('SessionLink Management Component', () => {
  let comp: SessionLinkComponent;
  let fixture: ComponentFixture<SessionLinkComponent>;
  let service: SessionLinkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'session-link', component: SessionLinkComponent }]),
        HttpClientTestingModule,
        SessionLinkComponent,
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
      .overrideTemplate(SessionLinkComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionLinkComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SessionLinkService);

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
    expect(comp.sessionLinks?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to sessionLinkService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSessionLinkIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSessionLinkIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
