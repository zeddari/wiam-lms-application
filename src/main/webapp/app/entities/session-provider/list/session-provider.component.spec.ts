import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionProviderService } from '../service/session-provider.service';

import { SessionProviderComponent } from './session-provider.component';

describe('SessionProvider Management Component', () => {
  let comp: SessionProviderComponent;
  let fixture: ComponentFixture<SessionProviderComponent>;
  let service: SessionProviderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'session-provider', component: SessionProviderComponent }]),
        HttpClientTestingModule,
        SessionProviderComponent,
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
      .overrideTemplate(SessionProviderComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionProviderComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SessionProviderService);

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
    expect(comp.sessionProviders?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to sessionProviderService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSessionProviderIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSessionProviderIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
