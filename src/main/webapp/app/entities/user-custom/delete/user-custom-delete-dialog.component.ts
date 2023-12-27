import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IUserCustom } from '../user-custom.model';
import { UserCustomService } from '../service/user-custom.service';

@Component({
  standalone: true,
  templateUrl: './user-custom-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class UserCustomDeleteDialogComponent {
  userCustom?: IUserCustom;

  constructor(
    protected userCustomService: UserCustomService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userCustomService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
