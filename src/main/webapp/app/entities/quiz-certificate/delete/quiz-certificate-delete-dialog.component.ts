import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IQuizCertificate } from '../quiz-certificate.model';
import { QuizCertificateService } from '../service/quiz-certificate.service';

@Component({
  standalone: true,
  templateUrl: './quiz-certificate-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class QuizCertificateDeleteDialogComponent {
  quizCertificate?: IQuizCertificate;

  constructor(
    protected quizCertificateService: QuizCertificateService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.quizCertificateService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
