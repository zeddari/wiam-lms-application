import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICountry2 } from '../country-2.model';
import { Country2Service } from '../service/country-2.service';

@Component({
  standalone: true,
  templateUrl: './country-2-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class Country2DeleteDialogComponent {
  country2?: ICountry2;

  constructor(
    protected country2Service: Country2Service,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.country2Service.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
