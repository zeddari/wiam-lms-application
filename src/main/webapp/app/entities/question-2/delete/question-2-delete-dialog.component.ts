import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IQuestion2 } from '../question-2.model';
import { Question2Service } from '../service/question-2.service';

@Component({
  standalone: true,
  templateUrl: './question-2-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class Question2DeleteDialogComponent {
  question2?: IQuestion2;

  constructor(
    protected question2Service: Question2Service,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.question2Service.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
