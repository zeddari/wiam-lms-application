import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClassroomDetailComponent } from './classroom-detail.component';

describe('Classroom Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClassroomDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: ClassroomDetailComponent,
              resolve: { classroom: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ClassroomDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load classroom on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ClassroomDetailComponent);

      // THEN
      expect(instance.classroom).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
