import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SponsorService } from '../service/sponsor.service';

import { SponsorComponent } from './sponsor.component';

describe('Sponsor Management Component', () => {
  let comp: SponsorComponent;
  let fixture: ComponentFixture<SponsorComponent>;
  let service: SponsorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'sponsor', component: SponsorComponent }]),
        HttpClientTestingModule,
        SponsorComponent,
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
      .overrideTemplate(SponsorComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SponsorComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SponsorService);

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
    expect(comp.sponsors?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to sponsorService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getSponsorIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getSponsorIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
