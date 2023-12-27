import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FollowUpService } from '../service/follow-up.service';

import { FollowUpComponent } from './follow-up.component';

describe('FollowUp Management Component', () => {
  let comp: FollowUpComponent;
  let fixture: ComponentFixture<FollowUpComponent>;
  let service: FollowUpService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'follow-up', component: FollowUpComponent }]),
        HttpClientTestingModule,
        FollowUpComponent,
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
      .overrideTemplate(FollowUpComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FollowUpComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FollowUpService);

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
    expect(comp.followUps?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to followUpService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getFollowUpIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getFollowUpIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
