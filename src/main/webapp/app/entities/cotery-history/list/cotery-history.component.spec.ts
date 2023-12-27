import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CoteryHistoryService } from '../service/cotery-history.service';

import { CoteryHistoryComponent } from './cotery-history.component';

describe('CoteryHistory Management Component', () => {
  let comp: CoteryHistoryComponent;
  let fixture: ComponentFixture<CoteryHistoryComponent>;
  let service: CoteryHistoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'cotery-history', component: CoteryHistoryComponent }]),
        HttpClientTestingModule,
        CoteryHistoryComponent,
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
      .overrideTemplate(CoteryHistoryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CoteryHistoryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CoteryHistoryService);

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
    expect(comp.coteryHistories?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to coteryHistoryService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCoteryHistoryIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCoteryHistoryIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
