import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { ISessionJoinMode } from '../session-join-mode.model';

@Component({
  standalone: true,
  selector: 'jhi-session-join-mode-detail',
  templateUrl: './session-join-mode-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class SessionJoinModeDetailComponent {
  @Input() sessionJoinMode: ISessionJoinMode | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  previousState(): void {
    window.history.back();
  }
}
