import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDiplomaType } from '../diploma-type.model';
import { DiplomaTypeService } from '../service/diploma-type.service';

@Component({
  standalone: true,
  templateUrl: './diploma-type-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DiplomaTypeDeleteDialogComponent {
  diplomaType?: IDiplomaType;

  constructor(
    protected diplomaTypeService: DiplomaTypeService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.diplomaTypeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
