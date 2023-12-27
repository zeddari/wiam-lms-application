import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CurrencyService } from '../service/currency.service';

import { CurrencyComponent } from './currency.component';

describe('Currency Management Component', () => {
  let comp: CurrencyComponent;
  let fixture: ComponentFixture<CurrencyComponent>;
  let service: CurrencyService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'currency', component: CurrencyComponent }]),
        HttpClientTestingModule,
        CurrencyComponent,
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
      .overrideTemplate(CurrencyComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CurrencyComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CurrencyService);

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
    expect(comp.currencies?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to currencyService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCurrencyIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCurrencyIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
