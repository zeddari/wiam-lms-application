import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FollowUpDetailComponent } from './follow-up-detail.component';

describe('FollowUp Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FollowUpDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: FollowUpDetailComponent,
              resolve: { followUp: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(FollowUpDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load followUp on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', FollowUpDetailComponent);

      // THEN
      expect(instance.followUp).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
