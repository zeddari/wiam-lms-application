import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISponsoring } from '../sponsoring.model';
import { SponsoringService } from '../service/sponsoring.service';

@Component({
  standalone: true,
  templateUrl: './sponsoring-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SponsoringDeleteDialogComponent {
  sponsoring?: ISponsoring;

  constructor(
    protected sponsoringService: SponsoringService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sponsoringService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
