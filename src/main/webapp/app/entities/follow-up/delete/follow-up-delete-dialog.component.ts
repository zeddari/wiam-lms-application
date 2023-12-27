import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IFollowUp } from '../follow-up.model';
import { FollowUpService } from '../service/follow-up.service';

@Component({
  standalone: true,
  templateUrl: './follow-up-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class FollowUpDeleteDialogComponent {
  followUp?: IFollowUp;

  constructor(
    protected followUpService: FollowUpService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.followUpService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
