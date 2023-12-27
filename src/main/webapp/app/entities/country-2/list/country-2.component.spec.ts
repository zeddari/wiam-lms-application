import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { Country2Service } from '../service/country-2.service';

import { Country2Component } from './country-2.component';

describe('Country2 Management Component', () => {
  let comp: Country2Component;
  let fixture: ComponentFixture<Country2Component>;
  let service: Country2Service;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'country-2', component: Country2Component }]),
        HttpClientTestingModule,
        Country2Component,
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
      .overrideTemplate(Country2Component, '')
      .compileComponents();

    fixture = TestBed.createComponent(Country2Component);
    comp = fixture.componentInstance;
    service = TestBed.inject(Country2Service);

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
    expect(comp.country2s?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to country2Service', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCountry2Identifier');
      const id = comp.trackId(0, entity);
      expect(service.getCountry2Identifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
