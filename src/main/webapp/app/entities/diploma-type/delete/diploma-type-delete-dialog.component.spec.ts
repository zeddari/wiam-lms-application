jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { DiplomaTypeService } from '../service/diploma-type.service';

import { DiplomaTypeDeleteDialogComponent } from './diploma-type-delete-dialog.component';

describe('DiplomaType Management Delete Component', () => {
  let comp: DiplomaTypeDeleteDialogComponent;
  let fixture: ComponentFixture<DiplomaTypeDeleteDialogComponent>;
  let service: DiplomaTypeService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, DiplomaTypeDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(DiplomaTypeDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DiplomaTypeDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(DiplomaTypeService);
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
