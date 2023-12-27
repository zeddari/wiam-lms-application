import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICoteryHistory } from '../cotery-history.model';
import { CoteryHistoryService } from '../service/cotery-history.service';

@Component({
  standalone: true,
  templateUrl: './cotery-history-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CoteryHistoryDeleteDialogComponent {
  coteryHistory?: ICoteryHistory;

  constructor(
    protected coteryHistoryService: CoteryHistoryService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.coteryHistoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
