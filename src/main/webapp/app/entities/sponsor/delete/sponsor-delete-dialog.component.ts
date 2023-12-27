import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISponsor } from '../sponsor.model';
import { SponsorService } from '../service/sponsor.service';

@Component({
  standalone: true,
  templateUrl: './sponsor-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SponsorDeleteDialogComponent {
  sponsor?: ISponsor;

  constructor(
    protected sponsorService: SponsorService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sponsorService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
