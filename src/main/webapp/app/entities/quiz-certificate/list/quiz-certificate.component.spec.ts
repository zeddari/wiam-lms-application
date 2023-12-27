import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { QuizCertificateService } from '../service/quiz-certificate.service';

import { QuizCertificateComponent } from './quiz-certificate.component';

describe('QuizCertificate Management Component', () => {
  let comp: QuizCertificateComponent;
  let fixture: ComponentFixture<QuizCertificateComponent>;
  let service: QuizCertificateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'quiz-certificate', component: QuizCertificateComponent }]),
        HttpClientTestingModule,
        QuizCertificateComponent,
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
      .overrideTemplate(QuizCertificateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuizCertificateComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(QuizCertificateService);

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
    expect(comp.quizCertificates?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to quizCertificateService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getQuizCertificateIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getQuizCertificateIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
