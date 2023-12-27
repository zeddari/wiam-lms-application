jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { FollowUpService } from '../service/follow-up.service';

import { FollowUpDeleteDialogComponent } from './follow-up-delete-dialog.component';

describe('FollowUp Management Delete Component', () => {
  let comp: FollowUpDeleteDialogComponent;
  let fixture: ComponentFixture<FollowUpDeleteDialogComponent>;
  let service: FollowUpService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, FollowUpDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(FollowUpDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FollowUpDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FollowUpService);
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
