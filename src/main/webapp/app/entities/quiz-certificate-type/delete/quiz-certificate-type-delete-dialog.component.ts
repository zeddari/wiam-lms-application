import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IQuizCertificateType } from '../quiz-certificate-type.model';
import { QuizCertificateTypeService } from '../service/quiz-certificate-type.service';

@Component({
  standalone: true,
  templateUrl: './quiz-certificate-type-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class QuizCertificateTypeDeleteDialogComponent {
  quizCertificateType?: IQuizCertificateType;

  constructor(
    protected quizCertificateTypeService: QuizCertificateTypeService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.quizCertificateTypeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
