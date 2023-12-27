import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPart } from '../part.model';
import { PartService } from '../service/part.service';

@Component({
  standalone: true,
  templateUrl: './part-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PartDeleteDialogComponent {
  part?: IPart;

  constructor(
    protected partService: PartService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.partService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
