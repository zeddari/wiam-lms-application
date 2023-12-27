import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DiplomaTypeService } from '../service/diploma-type.service';

import { DiplomaTypeComponent } from './diploma-type.component';

describe('DiplomaType Management Component', () => {
  let comp: DiplomaTypeComponent;
  let fixture: ComponentFixture<DiplomaTypeComponent>;
  let service: DiplomaTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'diploma-type', component: DiplomaTypeComponent }]),
        HttpClientTestingModule,
        DiplomaTypeComponent,
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
      .overrideTemplate(DiplomaTypeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DiplomaTypeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DiplomaTypeService);

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
    expect(comp.diplomaTypes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to diplomaTypeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getDiplomaTypeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getDiplomaTypeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
