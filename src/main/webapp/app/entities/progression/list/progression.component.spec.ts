import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProgressionService } from '../service/progression.service';

import { ProgressionComponent } from './progression.component';

describe('Progression Management Component', () => {
  let comp: ProgressionComponent;
  let fixture: ComponentFixture<ProgressionComponent>;
  let service: ProgressionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'progression', component: ProgressionComponent }]),
        HttpClientTestingModule,
        ProgressionComponent,
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
      .overrideTemplate(ProgressionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgressionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProgressionService);

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
    expect(comp.progressions?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to progressionService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getProgressionIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getProgressionIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
