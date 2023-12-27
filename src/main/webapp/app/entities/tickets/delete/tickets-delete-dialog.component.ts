import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITickets } from '../tickets.model';
import { TicketsService } from '../service/tickets.service';

@Component({
  standalone: true,
  templateUrl: './tickets-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TicketsDeleteDialogComponent {
  tickets?: ITickets;

  constructor(
    protected ticketsService: TicketsService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.ticketsService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
