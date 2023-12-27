import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IEnrolement } from '../enrolement.model';
import { EnrolementService } from '../service/enrolement.service';

@Component({
  standalone: true,
  templateUrl: './enrolement-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class EnrolementDeleteDialogComponent {
  enrolement?: IEnrolement;

  constructor(
    protected enrolementService: EnrolementService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.enrolementService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
