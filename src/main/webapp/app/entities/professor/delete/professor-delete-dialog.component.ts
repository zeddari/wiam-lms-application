import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IProfessor } from '../professor.model';
import { ProfessorService } from '../service/professor.service';

@Component({
  standalone: true,
  templateUrl: './professor-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ProfessorDeleteDialogComponent {
  professor?: IProfessor;

  constructor(
    protected professorService: ProfessorService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.professorService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
