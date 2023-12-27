import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { TopicDetailComponent } from './topic-detail.component';

describe('Topic Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TopicDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TopicDetailComponent,
              resolve: { topic: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TopicDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load topic on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TopicDetailComponent);

      // THEN
      expect(instance.topic).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
