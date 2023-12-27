import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { QuizCertificateTypeService } from '../service/quiz-certificate-type.service';

import { QuizCertificateTypeComponent } from './quiz-certificate-type.component';

describe('QuizCertificateType Management Component', () => {
  let comp: QuizCertificateTypeComponent;
  let fixture: ComponentFixture<QuizCertificateTypeComponent>;
  let service: QuizCertificateTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'quiz-certificate-type', component: QuizCertificateTypeComponent }]),
        HttpClientTestingModule,
        QuizCertificateTypeComponent,
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
      .overrideTemplate(QuizCertificateTypeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuizCertificateTypeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(QuizCertificateTypeService);

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
    expect(comp.quizCertificateTypes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to quizCertificateTypeService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getQuizCertificateTypeIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getQuizCertificateTypeIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
