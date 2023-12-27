import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProgressionModeService } from '../service/progression-mode.service';

import { ProgressionModeComponent } from './progression-mode.component';

describe('ProgressionMode Management Component', () => {
  let comp: ProgressionModeComponent;
  let fixture: ComponentFixture<ProgressionModeComponent>;
  let service: ProgressionModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'progression-mode', component: ProgressionModeComponent }]),
        HttpClientTestingModule,
        ProgressionModeComponent,
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
      .overrideTemplate(ProgressionModeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProgressionModeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProgressionModeService);

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
    expect(comp.progressionModes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to progressionModeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getProgressionModeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getProgressionModeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
