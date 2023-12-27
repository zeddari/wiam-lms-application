import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IQuizCertificateType } from '../quiz-certificate-type.model';

@Component({
  standalone: true,
  selector: 'jhi-quiz-certificate-type-detail',
  templateUrl: './quiz-certificate-type-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class QuizCertificateTypeDetailComponent {
  @Input() quizCertificateType: IQuizCertificateType | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
