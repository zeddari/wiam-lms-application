jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { QuizCertificateService } from '../service/quiz-certificate.service';

import { QuizCertificateDeleteDialogComponent } from './quiz-certificate-delete-dialog.component';

describe('QuizCertificate Management Delete Component', () => {
  let comp: QuizCertificateDeleteDialogComponent;
  let fixture: ComponentFixture<QuizCertificateDeleteDialogComponent>;
  let service: QuizCertificateService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, QuizCertificateDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(QuizCertificateDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(QuizCertificateDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(QuizCertificateService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      }),
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
