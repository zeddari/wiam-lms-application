import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TicketSubjectsDetailComponent } from './ticket-subjects-detail.component';

describe('TicketSubjects Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketSubjectsDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TicketSubjectsDetailComponent,
              resolve: { ticketSubjects: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TicketSubjectsDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load ticketSubjects on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TicketSubjectsDetailComponent);

      // THEN
      expect(instance.ticketSubjects).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
