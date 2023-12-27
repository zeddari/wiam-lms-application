import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IProgressionMode } from '../progression-mode.model';
import { ProgressionModeService } from '../service/progression-mode.service';

@Component({
  standalone: true,
  templateUrl: './progression-mode-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ProgressionModeDeleteDialogComponent {
  progressionMode?: IProgressionMode;

  constructor(
    protected progressionModeService: ProgressionModeService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.progressionModeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
