import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IProgression } from '../progression.model';
import { ProgressionService } from '../service/progression.service';

@Component({
  standalone: true,
  templateUrl: './progression-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ProgressionDeleteDialogComponent {
  progression?: IProgression;

  constructor(
    protected progressionService: ProgressionService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.progressionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
