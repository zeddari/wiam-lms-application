import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISessionLink } from '../session-link.model';
import { SessionLinkService } from '../service/session-link.service';

@Component({
  standalone: true,
  templateUrl: './session-link-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SessionLinkDeleteDialogComponent {
  sessionLink?: ISessionLink;

  constructor(
    protected sessionLinkService: SessionLinkService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sessionLinkService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
