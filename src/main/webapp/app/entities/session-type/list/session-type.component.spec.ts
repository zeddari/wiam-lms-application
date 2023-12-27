import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SessionTypeService } from '../service/session-type.service';

import { SessionTypeComponent } from './session-type.component';

describe('SessionType Management Component', () => {
  let comp: SessionTypeComponent;
  let fixture: ComponentFixture<SessionTypeComponent>;
  let service: SessionTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'session-type', component: SessionTypeComponent }]),
        HttpClientTestingModule,
        SessionTypeComponent,
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
      .overrideTemplate(SessionTypeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionTypeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SessionTypeService);

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
    expect(comp.sessionTypes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to sessionTypeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSessionTypeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSessionTypeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
