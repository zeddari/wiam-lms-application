jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { SessionJoinModeService } from '../service/session-join-mode.service';

import { SessionJoinModeDeleteDialogComponent } from './session-join-mode-delete-dialog.component';

describe('SessionJoinMode Management Delete Component', () => {
  let comp: SessionJoinModeDeleteDialogComponent;
  let fixture: ComponentFixture<SessionJoinModeDeleteDialogComponent>;
  let service: SessionJoinModeService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, SessionJoinModeDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(SessionJoinModeDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SessionJoinModeDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(SessionJoinModeService);
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
