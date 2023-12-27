import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../quiz.test-samples';

import { QuizFormService } from './quiz-form.service';

describe('Quiz Form Service', () => {
  let service: QuizFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuizFormService);
  });

  describe('Service methods', () => {
    describe('createQuizFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createQuizFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quizTitle: expect.any(Object),
            quizType: expect.any(Object),
            quizDescription: expect.any(Object),
            questions: expect.any(Object),
          }),
        );
      });

      it('passing IQuiz should create a new form with FormGroup', () => {
        const formGroup = service.createQuizFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quizTitle: expect.any(Object),
            quizType: expect.any(Object),
            quizDescription: expect.any(Object),
            questions: expect.any(Object),
          }),
        );
      });
    });

    describe('getQuiz', () => {
      it('should return NewQuiz for default Quiz initial value', () => {
        const formGroup = service.createQuizFormGroup(sampleWithNewData);

        const quiz = service.getQuiz(formGroup) as any;

        expect(quiz).toMatchObject(sampleWithNewData);
      });

      it('should return NewQuiz for empty Quiz initial value', () => {
        const formGroup = service.createQuizFormGroup();

        const quiz = service.getQuiz(formGroup) as any;

        expect(quiz).toMatchObject({});
      });

      it('should return IQuiz', () => {
        const formGroup = service.createQuizFormGroup(sampleWithRequiredData);

        const quiz = service.getQuiz(formGroup) as any;

        expect(quiz).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IQuiz should not enable id FormControl', () => {
        const formGroup = service.createQuizFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewQuiz should disable id FormControl', () => {
        const formGroup = service.createQuizFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
