import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITicketSubjects } from '../ticket-subjects.model';
import { TicketSubjectsService } from '../service/ticket-subjects.service';

@Component({
  standalone: true,
  templateUrl: './ticket-subjects-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TicketSubjectsDeleteDialogComponent {
  ticketSubjects?: ITicketSubjects;

  constructor(
    protected ticketSubjectsService: TicketSubjectsService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ticketSubjectsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
