import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DepartementService } from '../service/departement.service';

import { DepartementComponent } from './departement.component';

describe('Departement Management Component', () => {
  let comp: DepartementComponent;
  let fixture: ComponentFixture<DepartementComponent>;
  let service: DepartementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'departement', component: DepartementComponent }]),
        HttpClientTestingModule,
        DepartementComponent,
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
      .overrideTemplate(DepartementComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DepartementComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DepartementService);

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
    expect(comp.departements?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to departementService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getDepartementIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getDepartementIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
