import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CoteryService } from '../service/cotery.service';

import { CoteryComponent } from './cotery.component';

describe('Cotery Management Component', () => {
  let comp: CoteryComponent;
  let fixture: ComponentFixture<CoteryComponent>;
  let service: CoteryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'cotery', component: CoteryComponent }]), HttpClientTestingModule, CoteryComponent],
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
      .overrideTemplate(CoteryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CoteryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CoteryService);

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
    expect(comp.coteries?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to coteryService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCoteryIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCoteryIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
