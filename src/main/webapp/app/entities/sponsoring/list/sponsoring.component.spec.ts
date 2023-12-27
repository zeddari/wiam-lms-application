import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SponsoringService } from '../service/sponsoring.service';

import { SponsoringComponent } from './sponsoring.component';

describe('Sponsoring Management Component', () => {
  let comp: SponsoringComponent;
  let fixture: ComponentFixture<SponsoringComponent>;
  let service: SponsoringService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'sponsoring', component: SponsoringComponent }]),
        HttpClientTestingModule,
        SponsoringComponent,
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
      .overrideTemplate(SponsoringComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SponsoringComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SponsoringService);

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
    expect(comp.sponsorings?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to sponsoringService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSponsoringIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSponsoringIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
