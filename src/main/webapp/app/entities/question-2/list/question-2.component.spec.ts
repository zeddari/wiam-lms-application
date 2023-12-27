import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { Question2Service } from '../service/question-2.service';

import { Question2Component } from './question-2.component';

describe('Question2 Management Component', () => {
  let comp: Question2Component;
  let fixture: ComponentFixture<Question2Component>;
  let service: Question2Service;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'question-2', component: Question2Component }]),
        HttpClientTestingModule,
        Question2Component,
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
      .overrideTemplate(Question2Component, '')
      .compileComponents();

    fixture = TestBed.createComponent(Question2Component);
    comp = fixture.componentInstance;
    service = TestBed.inject(Question2Service);

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
    expect(comp.question2s?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to question2Service', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getQuestion2Identifier');
      const id = comp.trackId(0, entity);
      expect(service.getQuestion2Identifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
