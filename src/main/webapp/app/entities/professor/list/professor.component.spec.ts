import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProfessorService } from '../service/professor.service';

import { ProfessorComponent } from './professor.component';

describe('Professor Management Component', () => {
  let comp: ProfessorComponent;
  let fixture: ComponentFixture<ProfessorComponent>;
  let service: ProfessorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'professor', component: ProfessorComponent }]),
        HttpClientTestingModule,
        ProfessorComponent,
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
      .overrideTemplate(ProfessorComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfessorComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProfessorService);

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
    expect(comp.professors?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to professorService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getProfessorIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getProfessorIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
