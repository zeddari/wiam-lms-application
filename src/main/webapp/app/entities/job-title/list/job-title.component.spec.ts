import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { JobTitleService } from '../service/job-title.service';

import { JobTitleComponent } from './job-title.component';

describe('JobTitle Management Component', () => {
  let comp: JobTitleComponent;
  let fixture: ComponentFixture<JobTitleComponent>;
  let service: JobTitleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'job-title', component: JobTitleComponent }]),
        HttpClientTestingModule,
        JobTitleComponent,
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
      .overrideTemplate(JobTitleComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JobTitleComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(JobTitleService);

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
    expect(comp.jobTitles?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to jobTitleService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getJobTitleIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getJobTitleIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
