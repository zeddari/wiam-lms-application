import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISessionProvider } from '../session-provider.model';
import { SessionProviderService } from '../service/session-provider.service';

@Component({
  standalone: true,
  templateUrl: './session-provider-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SessionProviderDeleteDialogComponent {
  sessionProvider?: ISessionProvider;

  constructor(
    protected sessionProviderService: SessionProviderService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sessionProviderService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
