import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ICertificate } from '../certificate.model';
import { CertificateService } from '../service/certificate.service';

@Component({
  standalone: true,
  templateUrl: './certificate-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class CertificateDeleteDialogComponent {
  certificate?: ICertificate;

  constructor(
    protected certificateService: CertificateService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.certificateService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
