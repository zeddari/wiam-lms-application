jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { CoteryHistoryService } from '../service/cotery-history.service';

import { CoteryHistoryDeleteDialogComponent } from './cotery-history-delete-dialog.component';

describe('CoteryHistory Management Delete Component', () => {
  let comp: CoteryHistoryDeleteDialogComponent;
  let fixture: ComponentFixture<CoteryHistoryDeleteDialogComponent>;
  let service: CoteryHistoryService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, CoteryHistoryDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(CoteryHistoryDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CoteryHistoryDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CoteryHistoryService);
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
