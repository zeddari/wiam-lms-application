import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDiploma } from '../diploma.model';
import { DiplomaService } from '../service/diploma.service';

@Component({
  standalone: true,
  templateUrl: './diploma-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DiplomaDeleteDialogComponent {
  diploma?: IDiploma;

  constructor(
    protected diplomaService: DiplomaService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.diplomaService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
