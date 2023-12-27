import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IJobTitle } from '../job-title.model';
import { JobTitleService } from '../service/job-title.service';

@Component({
  standalone: true,
  templateUrl: './job-title-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class JobTitleDeleteDialogComponent {
  jobTitle?: IJobTitle;

  constructor(
    protected jobTitleService: JobTitleService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.jobTitleService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
