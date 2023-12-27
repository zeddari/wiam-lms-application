import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CertificateService } from '../service/certificate.service';

import { CertificateComponent } from './certificate.component';

describe('Certificate Management Component', () => {
  let comp: CertificateComponent;
  let fixture: ComponentFixture<CertificateComponent>;
  let service: CertificateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'certificate', component: CertificateComponent }]),
        HttpClientTestingModule,
        CertificateComponent,
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
      .overrideTemplate(CertificateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CertificateComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CertificateService);

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
    expect(comp.certificates?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to certificateService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCertificateIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCertificateIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
